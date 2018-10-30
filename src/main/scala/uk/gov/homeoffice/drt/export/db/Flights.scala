package uk.gov.homeoffice.drt.export.db

import java.time.ZonedDateTime

import scalikejdbc._


case class Flights(flightsId: Long, eventCode: String, arrivalPortCode: String, departurePortCode: String, voyagerNumber: String, carrierCode: String, scheduledDate: ZonedDateTime, passengers: Seq[Passengers] = Nil)

object Flights extends SQLSyntaxSupport[Flights] {
  override val tableName = "flights"

  implicit val session = AutoSession

  def insert(flights: Flights): Flights = {
    val flightsId = sql"insert into flights (event_code, arrival_port_code, departure_port_code, voyager_number, carrier_code, scheduled_date ) values (${flights.eventCode}, ${flights.arrivalPortCode}, ${flights.departurePortCode}, ${flights.voyagerNumber}, ${flights.carrierCode}, ${flights.scheduledDate})".updateAndReturnGeneratedKey.apply()
    flights.copy(flightsId = flightsId, passengers = flights.passengers.map(passengers => Passengers.insert(passengers, flightsId)))
  }

  def apply(f: SyntaxProvider[Flights])(rs: WrappedResultSet): Flights = apply(f.resultName)(rs)

  def apply(f: ResultName[Flights])(rs: WrappedResultSet): Flights =
    new Flights(
      rs.get(f.flightsId),
      rs.get(f.eventCode),
      rs.get(f.arrivalPortCode),
      rs.get(f.departurePortCode),
      rs.get(f.voyagerNumber),
      rs.get(f.carrierCode),
      rs.get(f.scheduledDate)
    )

  def apply(rs: WrappedResultSet) = new Flights(
    rs.long("FLIGHTS_ID"),
    rs.string("EVENT_CODE"),
    rs.string("ARRIVAL_PORT_CODE"),
    rs.string("DEPARTURE_PORT_CODE"),
    rs.string("VOYAGER_NUMBER"),
    rs.string("CARRIER_CODE"),
    rs.zonedDateTime("SCHEDULED_DATE"))

  val (f, p) = (Flights.syntax, Passengers.syntax)

  def allFlights: List[Flights] = sql"select * from flights".map(rs => Flights(rs)).list.apply()

  def flights: Seq[Flights] =
    withSQL {
      select.from(Flights as f).leftJoin(Passengers as p).on(f.flightsId, p.flightsId)
    }
      .one[Flights](Flights(f))
      .toMany(Passengers.opt(p))
      .map { (flights: Flights, passengersList) => flights.copy(passengers = passengersList) }
      .list
      .apply()
}

