package drt.export.db

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import uk.gov.homeoffice.drt.export.db._

class VoyageManifestsSpec extends Specification {

  "VoyageManifests" should {

    "can insert a voyageManifests with a passenger" in {
      val scheduledTime = new DateTime(2017, 11, 2, 3, 30)
      val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime.getMillis), ZoneId.of(scheduledTime.getZone.getID))

      new Database {}

      new CreateTables()

      val passengers = Seq(
        PassengerInfo(
          voyageManifestsId = 0L,
          documentType = Some("P"),
          documentIssuingCountryCode = "USA",
          eeaFlag = "1",
          age = None,
          disembarkationPortCountryCode = Some("UK"),
          nationalityCountryCode = Some("USA"),
          passengerIdentifier = Some("I"),
          inTransit = false
        )
      )
      val voyageManifests = VoyageManifests(id = 0L, eventCode = "DC", arrivalPortCode = "JFC", departurePortCode = "GAT", voyagerNumber = "001", carrierCode = "BA",
        scheduledDate = zonedDateTime, passengers)

      val savedVoyageManifests = VoyageManifests.insert(voyageManifests)

      val dbFlights = VoyageManifests.flights

      dbFlights.size mustEqual 1

      dbFlights.head mustEqual savedVoyageManifests

    }
  }

}
