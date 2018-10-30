package drt.export.db

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import uk.gov.homeoffice.drt.export.db._

class FlightsSpec extends Specification {

  "Flights" should {

    "can insert a flight with a passenger" in {
      val scheduledTime = new DateTime(2017, 11, 2, 3, 30)
      val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime.getMillis), ZoneId.of(scheduledTime.getZone.getID))

      val database = new Database(className = "org.h2.Driver", url = "jdbc:h2:mem:hello", user = "user", password = "pass")

      val tables = new CreateTables()

      val passengers = Seq(
        Passengers(
          flightsId = 0L,
          documentType = Some("P"),
          countryCode = "USA",
          age = None
        )
      )
      val flight = Flights(flightsId = 0L, eventCode = "DC", arrivalPortCode = "JFC", departurePortCode = "GAT", voyagerNumber = "001", carrierCode = "BA",
        scheduledDate = zonedDateTime, passengers)

      val savedFlight = Flights.insert(flight)

      val dbFlights = Flights.flights

      dbFlights.size mustEqual 1

      dbFlights.head mustEqual savedFlight

    }
  }

}
