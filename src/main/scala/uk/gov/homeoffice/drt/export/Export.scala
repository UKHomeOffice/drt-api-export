package uk.gov.homeoffice.drt

import grizzled.slf4j.Logging
import org.joda.time.DateTime
import uk.gov.homeoffice.drt.export.HasConfig
import uk.gov.homeoffice.drt.export.db.{CreateTables, Database, ProcessedZip, VoyageManifestPassengerInfo}
import uk.gov.homeoffice.drt.export.zip.{VoyageManifest, ZipFilesInDirectory}

import scala.util.{Failure, Try}

case class ExportProgress(totalZips: Int, startTime: DateTime, zips: Int, manifests: Int, passengers: Int) {
  override def toString: String = {
    f"${elapsedSeconds}s: $zips / $totalZips zips, $manifests manifests, $passengers pax. Speed: $secondsPerZip%.2fs/z. Time left: $timeRemaining, Zips left: ${totalZips - zips}"
  }

  def secondsPerZip: Double = elapsedSeconds.toDouble / zips

  def secondsRemaining: Int = ((totalZips - zips) * secondsPerZip).toInt

  def minutesRemaining: Int = secondsRemaining / 60

  def hoursRemaining: Int = minutesRemaining / 60

  def daysRemaining: Int = hoursRemaining / 24

  def timeRemaining: String = {
    if (daysRemaining > 0) s"$daysRemaining days"
    else if (hoursRemaining > 0) s"$hoursRemaining hrs"
    else if (minutesRemaining > 0) s"$minutesRemaining mins"
    else s"$secondsRemaining secs"
  }

  def elapsedMillis: Long = {
    val now = DateTime.now()
    now.getMillis - startTime.getMillis
  }

  def elapsedSeconds: Int = (elapsedMillis / 1000).toInt
}

object Export extends Logging with HasConfig {
  def run(startFileOption: Option[String], createTables: Boolean): Unit = {
    new Database {}
    if (createTables) new CreateTables

    val directory: String = config.getString("zipDirectory")

    val zipFiles = ZipFilesInDirectory(directory)
    val allFileNames = zipFiles.filesInDirectory(startFileOption).sorted
    info(s"${allFileNames.length} zips. Earliest: ${allFileNames.head}. Latest: ${allFileNames.takeRight(1).head}")

    exportZipFiles(zipFiles, allFileNames)
  }

  def exportZipFiles(zipFiles: ZipFilesInDirectory, allFileNames: List[String]): ExportProgress = allFileNames
    .foldLeft(ExportProgress(allFileNames.length, DateTime.now(), 0, 0, 0)) {
      case (progress, zipFileName) =>
        val toProcess = ProcessedZip(zipFileName)
        if (!ProcessedZip.processed(toProcess)) {
          val voyageManifests = zipFiles.unzipFile(zipFileName)
          exportManifests(voyageManifests)
          ProcessedZip.insert(toProcess)
          updateProgressAndLog(voyageManifests, progress)
        } else {
          info(s"Already processed $zipFileName. Skipping")
          progress.copy(totalZips = progress.totalZips - 1)
        }
    }

  def updateProgressAndLog(voyageManifests: List[VoyageManifest], progress: ExportProgress): ExportProgress = {
    val paxCount = voyageManifests.map(_.PassengerList.length).sum
    val manifestCount = voyageManifests.length
    val updatedProgress = progress.copy(zips = progress.zips + 1, manifests = progress.manifests + manifestCount, passengers = progress.passengers + paxCount)

    info(s"Progress: $updatedProgress")

    updatedProgress
  }

  def exportManifests(voyageManifests: List[VoyageManifest]): Unit = {
    val fileBatch = voyageManifests.flatMap(vm => {
      val passengers = vm.toDB
      removeAnyExistingEntries(passengers)
      toBatch(passengers)
    })
    Try {
      VoyageManifestPassengerInfo.batchInsert(fileBatch)
    }.recoverWith {
      case e =>
        error(s"rolling back. ${e.getMessage} ", e)
        Failure(e)
    }
  }

  def toBatch(passengers: List[VoyageManifestPassengerInfo]): Seq[Seq[Any]] = {
    passengers.map(p => Seq(
      p.eventCode,
      p.arrivalPortCode,
      p.departurePortCode,
      p.voyagerNumber,
      p.carrierCode,
      p.scheduledDate,
      p.documentType,
      p.documentIssuingCountryCode,
      p.eeaFlag,
      p.age,
      p.disembarkationPortCountryCode,
      p.nationalityCountryCode,
      p.passengerIdentifier,
      p.inTransit
    ))
  }

  def removeAnyExistingEntries(voyageManifestPassengerInfoList: List[VoyageManifestPassengerInfo]): Unit = voyageManifestPassengerInfoList
    .headOption
    .foreach(VoyageManifestPassengerInfo.deleteVoyage)
}