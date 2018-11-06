package uk.gov.homeoffice.drt.export.db

import java.time.ZonedDateTime
import scalikejdbc._
import sqls.count

case class VoyageManifestPassengerInfo(eventCode: String, arrivalPortCode: String, departurePortCode: String, voyagerNumber: String, carrierCode: String, scheduledDate: ZonedDateTime, documentType: Option[String], documentIssuingCountryCode: String, eeaFlag: String, age: Option[String], disembarkationPortCountryCode: Option[String], nationalityCountryCode: Option[String], passengerIdentifier: Option[String], inTransit: Boolean)

object VoyageManifestPassengerInfo extends SQLSyntaxSupport[VoyageManifestPassengerInfo] {
  override val tableName = "voyage_manifest_passenger_info"

  implicit val session = AutoSession

  def insert(voyageManifestPassengerInfo: VoyageManifestPassengerInfo): VoyageManifestPassengerInfo = {

    withSQL {
      val vm = VoyageManifestPassengerInfo.column
      insertInto(VoyageManifestPassengerInfo).namedValues(
        vm.eventCode -> voyageManifestPassengerInfo.eventCode,
        vm.arrivalPortCode -> voyageManifestPassengerInfo.arrivalPortCode,
        vm.departurePortCode -> voyageManifestPassengerInfo.departurePortCode,
        vm.voyagerNumber -> voyageManifestPassengerInfo.voyagerNumber,
        vm.carrierCode -> voyageManifestPassengerInfo.carrierCode,
        vm.scheduledDate -> voyageManifestPassengerInfo.scheduledDate,
        vm.documentType -> voyageManifestPassengerInfo.documentType,
        vm.documentIssuingCountryCode -> voyageManifestPassengerInfo.documentIssuingCountryCode,
        vm.eeaFlag -> voyageManifestPassengerInfo.eeaFlag,
        vm.age -> voyageManifestPassengerInfo.age,
        vm.disembarkationPortCountryCode -> voyageManifestPassengerInfo.disembarkationPortCountryCode,
        vm.nationalityCountryCode -> voyageManifestPassengerInfo.nationalityCountryCode,
        vm.passengerIdentifier -> voyageManifestPassengerInfo.passengerIdentifier,
        vm.inTransit -> voyageManifestPassengerInfo.inTransit
      )
    }.update.apply()

    voyageManifestPassengerInfo
  }

  def apply(v: SyntaxProvider[VoyageManifestPassengerInfo])(rs: WrappedResultSet): VoyageManifestPassengerInfo = apply(v.resultName)(rs)

  def apply(v: ResultName[VoyageManifestPassengerInfo])(rs: WrappedResultSet): VoyageManifestPassengerInfo =
    new VoyageManifestPassengerInfo(
      eventCode = rs.get(v.eventCode),
      arrivalPortCode = rs.get(v.arrivalPortCode),
      departurePortCode = rs.get(v.departurePortCode),
      voyagerNumber = rs.get(v.voyagerNumber),
      carrierCode = rs.get(v.carrierCode),
      scheduledDate = rs.get(v.scheduledDate),
      documentType = rs.get(v.documentType),
      documentIssuingCountryCode = rs.get(v.documentIssuingCountryCode),
      eeaFlag = rs.get(v.eeaFlag), age = rs.get(v.age),
      disembarkationPortCountryCode = rs.get(v.disembarkationPortCountryCode),
      nationalityCountryCode = rs.get(v.nationalityCountryCode),
      passengerIdentifier = rs.get(v.passengerIdentifier),
      inTransit = rs.get(v.inTransit)
    )

  val vm = VoyageManifestPassengerInfo.syntax

  def flights: Seq[VoyageManifestPassengerInfo] =
    withSQL {
      select.from(VoyageManifestPassengerInfo as vm)
    }
      .map[VoyageManifestPassengerInfo](VoyageManifestPassengerInfo(vm))
      .list
      .apply()

  def voyageExistInDatabase(vmpi: VoyageManifestPassengerInfo): Boolean =
    withSQL {
      select(count).from(VoyageManifestPassengerInfo as vm).where
        .eq(vm.eventCode, vmpi.eventCode)
        .and.eq(vm.arrivalPortCode, vmpi.arrivalPortCode)
        .and.eq(vm.departurePortCode, vmpi.departurePortCode)
        .and.eq(vm.voyagerNumber, vmpi.voyagerNumber)
        .and.eq(vm.scheduledDate, vmpi.scheduledDate)
    }.map(_.int(1)).single.apply() != Option(0)

  def deleteVoyage(vmpi: VoyageManifestPassengerInfo): Int = {
    withSQL {
      delete.from(VoyageManifestPassengerInfo as vm).where
        .eq(vm.eventCode, vmpi.eventCode)
        .and.eq( vm.arrivalPortCode, vmpi.arrivalPortCode)
        .and.eq(vm.departurePortCode, vmpi.departurePortCode)
        .and.eq(vm.voyagerNumber, vmpi.voyagerNumber)
        .and.eq(vm.scheduledDate, vmpi.scheduledDate)
    }.update.apply()
  }
}