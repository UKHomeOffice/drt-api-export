package uk.gov.homeoffice.drt.export.db

import grizzled.slf4j.Logging
import scalikejdbc.interpolation.SQLSyntax.count
import scalikejdbc._


case class ProcessedZip(name: String)

object ProcessedZip extends SQLSyntaxSupport[ProcessedZip] with Logging {
  override val tableName = "processed_zips"

  implicit val session: AutoSession.type = AutoSession

  val pz = ProcessedZip.syntax

  def insert(processedZip: ProcessedZip): Int = withSQL {
    val row = ProcessedZip.column
    insertInto(ProcessedZip).namedValues(row.name -> processedZip.name)
  }.update.apply()

  def processed(processedZip: ProcessedZip): Boolean = withSQL {
    select(count).from(ProcessedZip as pz).where
      .eq(pz.name, processedZip.name)
  }.map(x => x.int(1)).single.apply() != Option(0)
}
