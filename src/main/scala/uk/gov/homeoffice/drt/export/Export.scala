package uk.gov.homeoffice.drt

import grizzled.slf4j.Logging
import scalikejdbc.DB
import uk.gov.homeoffice.drt.export.HasConfig
import uk.gov.homeoffice.drt.export.db.{CreateTables, Database, VoyageManifestPassengerInfo}
import uk.gov.homeoffice.drt.export.zip.{VoyageManifest, ZipFilesInDirectory}

import scala.util.{Failure, Try}

object Export extends Logging with HasConfig {
  def run(startFileOption: Option[String], createTables: Boolean): Unit = {
    new Database {}
    if (createTables) new CreateTables

    val directory: String = config.getString("zipDirectory")

    val zipFiles = ZipFilesInDirectory(directory)
    val allFileNames = zipFiles.filesInDirectory(startFileOption)
    info(s"all file names $allFileNames")
    allFileNames.foreach(name => {
      info(s"unzipping $name")
      val voyageManifests: List[VoyageManifest] = zipFiles.unzipFile(name)
      info(s"saving $name")
      exportManifests(voyageManifests)
    })
  }

  def exportManifests(voyageManifests: List[VoyageManifest]): Unit = DB localTx { implicit session =>
    voyageManifests.foreach(exportManifest)
  }

  def exportManifest(vm: VoyageManifest): Try[List[VoyageManifestPassengerInfo]] = {
    Try {
      val voyageManifestPassengerInfoList = vm.toDB
      removeAnyExistingEntries(voyageManifestPassengerInfoList)
      voyageManifestPassengerInfoList.map(dbVM => VoyageManifestPassengerInfo.insert(dbVM))
    }.recoverWith {
      case e =>
        error(s"rolling back. ${e.getMessage} ", e)
        Failure(e)
    }
  }

  def removeAnyExistingEntries(voyageManifestPassengerInfoList: List[VoyageManifestPassengerInfo]): Unit = voyageManifestPassengerInfoList
    .headOption
    .foreach(vmpi => if (VoyageManifestPassengerInfo.voyageExistInDatabase(vmpi)) {
      info(s"deleting voyage manifest passenger info ${(vmpi.eventCode, vmpi.voyagerNumber, vmpi.arrivalPortCode, vmpi.departurePortCode, vmpi.scheduledDate)}")
      VoyageManifestPassengerInfo.deleteVoyage(vmpi)
    })

}