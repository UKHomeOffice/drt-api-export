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
    withSQL {
      val p = PassengerInfo.column
      insertInto(PassengerInfo).namedValues(
        p.voyageManifestId -> voyageManifestId,
        p.documentType -> passengers.documentType,
        p.documentIssuingCountryCode -> passengers.documentIssuingCountryCode,
        p.eeaFlag -> passengers.eeaFlag,
        p.age -> passengers.age,
        p.disembarkationPortCountryCode -> passengers.disembarkationPortCountryCode,
        p.nationalityCountryCode -> passengers.nationalityCountryCode,
        p.passengerIdentifier -> passengers.passengerIdentifier,
        p.inTransit -> passengers.inTransit
      )

    }.update().apply()
    passengers.copy(voyageManifestId = voyageManifestId)
  }
}
