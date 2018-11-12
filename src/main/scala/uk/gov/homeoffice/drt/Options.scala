package uk.gov.homeoffice.drt

import scopt.OptionParser

object Options {
  def apply(programName: String): OptionParser[ParsedArguments] = {
    new scopt.OptionParser[ParsedArguments](programName) {
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
  }
}
