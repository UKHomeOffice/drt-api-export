package uk.gov.homeoffice.drt

import grizzled.slf4j.Logging
import org.postgresql.util.PSQLException
import scalikejdbc.DB
import uk.gov.homeoffice.drt.export.HasConfig
import uk.gov.homeoffice.drt.export.db.{CreateTables, Database, VoyageManifest}
import uk.gov.homeoffice.drt.export.zip.ZipFilesInDirectory

import scala.util.{Failure, Success, Try}

object Boot extends App with Logging with HasConfig {
  info(s"Starting API Export.")

  val parser = new scopt.OptionParser[ParsedArguments]("drt-api-export") {
    head("drt-api-export", "1.0")

    cmd("start")
      .action((_, c) => c.copy(command = Start))
      .text("exports api data")
      .children(
        opt[String](name = "startFile")
          .optional()
          .text(s"DQ zip file to start at")
          .action((startFile, c) => c.copy(startFile = Some(startFile))),
        opt[Boolean](name = "createTables")
          .optional()
          .text(s"true to create table structure")
          .action((createTables, c) => c.copy(createTables = createTables))
      )


    override def showUsageOnError = true
  }

  parser.parse(args, ParsedArguments()) match {
    case Some(ParsedArguments(Start, startFileOption, createTables)) =>
      info("starting api export")
      new Database{}
      if (createTables) new CreateTables

      val zipFiles = new ZipFilesInDirectory{}
      val allFileNames = zipFiles.getFilesInDirectory(startFileOption)
      info(s"all file names $allFileNames")
      allFileNames.map(name => {
        DB localTx { implicit session =>
          info(s"unzipping $name")
          val voyageManifests = zipFiles.unzipFile(name)
          info(s"saving to db $name")
          voyageManifests.map(vm =>
            Try {
              vm.toDB.map(dbVM => VoyageManifest.insert(dbVM))
            }.recoverWith {
              case e: PSQLException if e.getMessage.contains("already exists")  =>
                error(s"continuing as relation exists", e)
                Success(None)
              case e =>
                error(s"not continuing rolling back. ${e.getMessage} ", e)
                Failure(e)
            }
          )
        }
      })

    case _ =>
      parser.showUsage()

  }

}
sealed trait Command
case object ShowUsage extends Command
case object Start extends Command

case class ParsedArguments(command: Command = ShowUsage, startFile: Option[String] = None,createTables: Boolean = false)