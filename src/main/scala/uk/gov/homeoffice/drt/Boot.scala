package uk.gov.homeoffice.drt

import grizzled.slf4j.Logging
import org.slf4j.LoggerFactory

object Boot extends App with Logging {
  info(s"Starting API Export.")


  val parser = new scopt.OptionParser[ParsedArguments]("drt-api-export") {
    head("drt-api-export", "1.0")

    cmd("start")
      .action((_, c) => c.copy(command = Start))
      .text("exports api data")

    override def showUsageOnError = true
  }

  parser.parse(args, ParsedArguments()) match {
    case Some(ParsedArguments(Start)) =>
      info("starting something")

    case Some(_) =>
      parser.showUsage()

  }

}
sealed trait Command
case object ShowUsage extends Command
case object Start extends Command

case class ParsedArguments(command: Command = ShowUsage)