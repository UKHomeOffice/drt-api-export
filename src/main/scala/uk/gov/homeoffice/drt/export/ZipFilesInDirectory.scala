package uk.gov.homeoffice.drt.export

import grizzled.slf4j.Logging

import scala.util.matching.Regex

trait ZipFilesInDirectory extends Logging {

  val dqRegex: Regex = "(drt_dq_[0-9]{6}_[0-9]{6})(_[0-9]{4}\\.zip)".r

  val directory: String

  def getSmallestFileInDirectory: String = {

    "blah"
  }


}
