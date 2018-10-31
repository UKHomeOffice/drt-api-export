package uk.gov.homeoffice.drt.export.db

import scalikejdbc._

case class PassengerInfo(voyageManifestId: Long, documentType: Option[String], documentIssuingCountryCode: String, eeaFlag: String, age: Option[String], disembarkationPortCountryCode: Option[String], nationalityCountryCode: Option[String], passengerIdentifier: Option[String], inTransit: Boolean)

object PassengerInfo extends SQLSyntaxSupport[PassengerInfo] {
  override val tableName = "passenger_info"

  implicit val session = AutoSession

  def opt(m: SyntaxProvider[PassengerInfo])(rs: WrappedResultSet): Option[PassengerInfo] =
    rs.longOpt(m.resultName.voyageManifestId).map(_ => PassengerInfo(m)(rs))

  def apply(m: SyntaxProvider[PassengerInfo])(rs: WrappedResultSet): PassengerInfo = apply(m.resultName)(rs)

  def apply(p: ResultName[PassengerInfo])(rs: WrappedResultSet): PassengerInfo =
    new PassengerInfo(rs.get(p.voyageManifestId), rs.get(p.documentType), rs.get(p.documentIssuingCountryCode), rs.get(p.eeaFlag), rs.get(p.age), rs.get(p.disembarkationPortCountryCode), rs.get(p.nationalityCountryCode), rs.get(p.passengerIdentifier), rs.get(p.inTransit))

  def insert(passengers: PassengerInfo, voyageManifestId: Long): PassengerInfo = {
    sql"insert into passenger_info (voyage_manifest_id, document_type, document_issuing_country_code, eea_flag, age, disembarkation_port_country_code, nationality_country_code, passenger_identifier, in_transit) values (${voyageManifestId}, ${passengers.documentType}, ${passengers.documentIssuingCountryCode}, ${passengers.eeaFlag}, ${passengers.age}, ${passengers.disembarkationPortCountryCode}, ${passengers.nationalityCountryCode}, ${passengers.passengerIdentifier}, ${passengers.inTransit})".update.apply()
    passengers.copy(voyageManifestId = voyageManifestId)
  }
}
