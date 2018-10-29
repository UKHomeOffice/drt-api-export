package uk.gov.homeoffice.drt.export

import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.ZipFile
import grizzled.slf4j.Logging
import org.apache.commons.io.IOUtils
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

trait ZipFilesInDirectory extends Logging {

  val dqRegex: Regex = "drt_dq_([0-9]{2})([0-9]{2})([0-9]{2})_[0-9]{6}_[0-9]{4}\\.zip".r

  val directory: String

  def getFilesInDirectory: List[String] = {
    val d = new File(directory)

    if (!d.exists && !d.isDirectory) {
      throw new IllegalArgumentException(s"Directory $directory should exist and be a directory.")
    }
    val files = d.listFiles.filter(f => f.isFile && f.getName.matches(dqRegex.regex)).sortBy(f => f.getName match {
      case dqRegex(day, month, year) => (year.toInt, month.toInt, day.toInt)
      case _ => (-1, -1, -1)
    }).toList

    files.map(_.getName)
  }

  import scala.collection.JavaConversions._

  def unzipFile(name: String): List[VoyageManifest] = {

    val zipFile = new ZipFile(s"$directory/$name")
    val entries = zipFile.entries().toList
    val content = entries.flatMap { entry =>
      val is = zipFile.getInputStream(entry)
      val voyageManifest = try
        jsonStringToManifest(IOUtils.toString(is, UTF_8))
      finally
        is.close()
      voyageManifest
    }

    content

  }

  def jsonStringToManifest(content: String): Option[VoyageManifest] = {
    parseVoyagePassengerInfo(content) match {
      case Success(m) =>
        if (m.EventCode == "DC") {
          info(s"Using ${m.EventCode} manifest for ${m.ArrivalPortCode} arrival ${m.flightCode}")
          Option(m)
        }
        else None
      case Failure(t) =>
        error(s"Failed to parse voyage manifest json", t)
        None
    }
  }

  def parseVoyagePassengerInfo(content: String): Try[VoyageManifest] = {
    import FlightPassengerInfoProtocol._
    import spray.json._
    Try(content.parseJson.convertTo[VoyageManifest])
  }


}
