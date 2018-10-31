package drt.export.db

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.joda.time.DateTime
import org.specs2.mutable.Specification
import uk.gov.homeoffice.drt.export.db._

class VoyageManifestSpec extends Specification {

  "VoyageManifest" should {

    "can insert a voyageManifest with a passenger" in {
      val scheduledTime = new DateTime(2017, 11, 2, 3, 30)
      val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime.getMillis), ZoneId.of(scheduledTime.getZone.getID))

      new Database {}

      new CreateTables()

      val passengers = Seq(
        PassengerInfo(
          voyageManifestId = 0L,
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
      val voyageManifest = VoyageManifest(id = 0L, eventCode = "DC", arrivalPortCode = "JFC", departurePortCode = "GAT", voyagerNumber = "001", carrierCode = "BA",
        scheduledDate = zonedDateTime, passengers)

      val savedVoyageManifests = VoyageManifest.insert(voyageManifest)

      val dbFlights = VoyageManifest.flights

      dbFlights.size mustEqual 1

      dbFlights.head mustEqual savedVoyageManifests

    }
  }

}