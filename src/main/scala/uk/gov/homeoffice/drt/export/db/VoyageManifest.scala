package uk.gov.homeoffice.drt.export.db

import java.time.ZonedDateTime

import scalikejdbc._


case class VoyageManifest(id: Long, eventCode: String, arrivalPortCode: String, departurePortCode: String, voyagerNumber: String, carrierCode: String, scheduledDate: ZonedDateTime, passengers: Seq[PassengerInfo] = Nil)

object VoyageManifest extends SQLSyntaxSupport[VoyageManifest] {
  override val tableName = "voyage_manifest"

  implicit val session = AutoSession

  def insert(voyageManifest: VoyageManifest): VoyageManifest = {

    val voyageManifestId = withSQL {
      val vm = VoyageManifest.column
      insertInto(VoyageManifest).namedValues(
        vm.eventCode -> voyageManifest.eventCode,
        vm.arrivalPortCode -> voyageManifest.arrivalPortCode,
        vm.departurePortCode -> voyageManifest.departurePortCode,
        vm.voyagerNumber -> voyageManifest.voyagerNumber,
        vm.carrierCode -> voyageManifest.carrierCode,
        vm.scheduledDate -> voyageManifest.scheduledDate
      )
    }.updateAndReturnGeneratedKey.apply()
    voyageManifest.copy(id = voyageManifestId, passengers = voyageManifest.passengers.map(passengers => PassengerInfo.insert(passengers, voyageManifestId)))
  }

  def apply(v: SyntaxProvider[VoyageManifest])(rs: WrappedResultSet): VoyageManifest = apply(v.resultName)(rs)

  def apply(v: ResultName[VoyageManifest])(rs: WrappedResultSet): VoyageManifest =
    new VoyageManifest(
      rs.get(v.id),
      rs.get(v.eventCode),
      rs.get(v.arrivalPortCode),
      rs.get(v.departurePortCode),
      rs.get(v.voyagerNumber),
      rs.get(v.carrierCode),
      rs.get(v.scheduledDate)
    )

  val (v, p) = (VoyageManifest.syntax, PassengerInfo.syntax)

  def flights: Seq[VoyageManifest] =
    withSQL {
      select.from(VoyageManifest as v).leftJoin(PassengerInfo as p).on(v.id, p.voyageManifestId)
    }
      .one[VoyageManifest](VoyageManifest(v))
      .toMany(PassengerInfo.opt(p))
      .map { (voyageManifest: VoyageManifest, passengers) => voyageManifest.copy(passengers = passengers) }
      .list
      .apply()
}

