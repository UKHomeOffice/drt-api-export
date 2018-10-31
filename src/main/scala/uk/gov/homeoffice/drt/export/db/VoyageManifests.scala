package uk.gov.homeoffice.drt.export.db

import java.time.ZonedDateTime

import scalikejdbc._


case class VoyageManifests(id: Long, eventCode: String, arrivalPortCode: String, departurePortCode: String, voyagerNumber: String, carrierCode: String, scheduledDate: ZonedDateTime, passengers: Seq[PassengerInfo] = Nil)

object VoyageManifests extends SQLSyntaxSupport[VoyageManifests] {
  override val tableName = "voyage_manifests"

  implicit val session = AutoSession

  def insert(voyageManifests: VoyageManifests): VoyageManifests = {
    val flightsId = sql"insert into voyage_manifests (event_code, arrival_port_code, departure_port_code, voyager_number, carrier_code, scheduled_date ) values (${voyageManifests.eventCode}, ${voyageManifests.arrivalPortCode}, ${voyageManifests.departurePortCode}, ${voyageManifests.voyagerNumber}, ${voyageManifests.carrierCode}, ${voyageManifests.scheduledDate})".updateAndReturnGeneratedKey.apply()
    voyageManifests.copy(id = flightsId, passengers = voyageManifests.passengers.map(passengers => PassengerInfo.insert(passengers, flightsId)))
  }

  def apply(v: SyntaxProvider[VoyageManifests])(rs: WrappedResultSet): VoyageManifests = apply(v.resultName)(rs)

  def apply(v: ResultName[VoyageManifests])(rs: WrappedResultSet): VoyageManifests =
    new VoyageManifests(
      rs.get(v.id),
      rs.get(v.eventCode),
      rs.get(v.arrivalPortCode),
      rs.get(v.departurePortCode),
      rs.get(v.voyagerNumber),
      rs.get(v.carrierCode),
      rs.get(v.scheduledDate)
    )

  val (v, p) = (VoyageManifests.syntax, PassengerInfo.syntax)

  def flights: Seq[VoyageManifests] =
    withSQL {
      select.from(VoyageManifests as v).leftJoin(PassengerInfo as p).on(v.id, p.voyageManifestsId)
    }
      .one[VoyageManifests](VoyageManifests(v))
      .toMany(PassengerInfo.opt(p))
      .map { (flights: VoyageManifests, passengersList) => flights.copy(passengers = passengersList) }
      .list
      .apply()
}

