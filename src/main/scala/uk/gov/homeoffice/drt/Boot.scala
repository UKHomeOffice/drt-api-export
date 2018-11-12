package uk.gov.homeoffice.drt

import grizzled.slf4j.Logging
import uk.gov.homeoffice.drt.export.HasConfig


object Boot extends App with Logging with HasConfig {
  info(s"Starting API Export.")

  Options("drt-api-export").parse(args, ParsedArguments()) match {
    case Some(ParsedArguments(Start, startFileOption, createTables)) =>
      info("starting api export")
      Export.run(startFileOption, createTables)

    case _ =>
      Options("drt-api-export").showUsage()
  }

}

sealed trait Command

case object ShowUsage extends Command

case object Start extends Command

case class ParsedArguments(command: Command = ShowUsage, startFile: Option[String] = None, createTables: Boolean = false)