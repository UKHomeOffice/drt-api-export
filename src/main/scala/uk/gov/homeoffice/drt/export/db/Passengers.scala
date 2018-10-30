package uk.gov.homeoffice.drt.export.db

import scalikejdbc._

case class Passengers(flightsId: Long, documentType: Option[String], countryCode: String, age: Option[String])

object Passengers extends SQLSyntaxSupport[Passengers] {
  override val tableName = "passengers"

  implicit val session = AutoSession

  def opt(m: SyntaxProvider[Passengers])(rs: WrappedResultSet): Option[Passengers] =
    rs.longOpt(m.resultName.flightsId).map(_ => Passengers(m)(rs))

  def apply(m: SyntaxProvider[Passengers])(rs: WrappedResultSet): Passengers = apply(m.resultName)(rs)

  def apply(p: ResultName[Passengers])(rs: WrappedResultSet): Passengers =
    new Passengers(rs.get(p.flightsId), rs.get(p.documentType), rs.get(p.countryCode), rs.get(p.age))

  def insert(passengers: Passengers, flightsId: Long): Passengers = {
    sql"insert into passengers (flights_id, document_type, country_code, age) values (${flightsId}, ${passengers.documentType}, ${passengers.countryCode}, ${passengers.age})".update.apply()
    passengers.copy(flightsId = flightsId)
  }
}
