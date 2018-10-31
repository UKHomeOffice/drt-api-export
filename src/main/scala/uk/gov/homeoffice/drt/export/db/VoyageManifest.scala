package uk.gov.homeoffice.drt.export.db

import java.time.ZonedDateTime

import scalikejdbc._


case class VoyageManifest(id: Long, eventCode: String, arrivalPortCode: String, departurePortCode: String, voyagerNumber: String, carrierCode: String, scheduledDate: ZonedDateTime, passengers: Seq[PassengerInfo] = Nil)

object VoyageManifest extends SQLSyntaxSupport[VoyageManifest] {
  override val tableName = "voyage_manifest"

  implicit val session = AutoSession

  def insert(voyageManifests: VoyageManifest): VoyageManifest = {
    val flightsId = sql"insert into voyage_manifest (event_code, arrival_port_code, departure_port_code, voyager_number, carrier_code, scheduled_date ) values (${voyageManifests.eventCode}, ${voyageManifests.arrivalPortCode}, ${voyageManifests.departurePortCode}, ${voyageManifests.voyagerNumber}, ${voyageManifests.carrierCode}, ${voyageManifests.scheduledDate})".updateAndReturnGeneratedKey.apply()
    voyageManifests.copy(id = flightsId, passengers = voyageManifests.passengers.map(passengers => PassengerInfo.insert(passengers, flightsId)))
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
      .map { (flights: VoyageManifest, passengersList) => flights.copy(passengers = passengersList) }
      .list
      .apply()
}

