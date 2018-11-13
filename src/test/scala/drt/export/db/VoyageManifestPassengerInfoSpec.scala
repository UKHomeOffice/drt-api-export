package drt.export.db

import java.time.{Instant, ZoneId, ZonedDateTime}

import org.joda.time.DateTime
import org.specs2.matcher.Scope
import org.specs2.mutable.Specification
import scalikejdbc._
import uk.gov.homeoffice.drt.Export
import uk.gov.homeoffice.drt.export.db._

import scala.util.{Success, Try}

class VoyageManifestPassengerInfoSpec extends Specification{
  sequential

  trait Context extends Scope {
    val scheduledTime = new DateTime(2017, 11, 2, 3, 30)
    val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(scheduledTime.getMillis), ZoneId.of(scheduledTime.getZone.getID))

    val database = new Database {
      override lazy val url: String = s"jdbc:h2:mem:hello-${scala.util.Random.nextInt()};MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
    }

    implicit val session = AutoSession

    new CreateTables()

    val voyageManifestPassengerInfo = VoyageManifestPassengerInfo(
      eventCode = "DC", arrivalPortCode = "JFC", departurePortCode = "GAT", voyagerNumber = "001", carrierCode = "BA",
      scheduledDate = zonedDateTime,
      documentType = Some("P"),
      documentIssuingCountryCode = "USA",
      eeaFlag = "1",
      age = None,
      disembarkationPortCountryCode = Some("UK"),
      nationalityCountryCode = Some("USA"),
      passengerIdentifier = Some("I"),
      inTransit = false
    )
  }

  "VoyageManifestPassengerInfoSpec" should {

    "can insert a voyageManifest with a passenger" in new Context {

      VoyageManifestPassengerInfo.batchInsert(Export.toBatch(List(voyageManifestPassengerInfo)))

      val dbFlights = VoyageManifestPassengerInfo.flights

      dbFlights.size mustEqual 1

      dbFlights.head mustEqual voyageManifestPassengerInfo
    }

    "voyageExist is true when the manifests exists" in new Context {

      VoyageManifestPassengerInfo.batchInsert(Export.toBatch(List(voyageManifestPassengerInfo)))

      val voyageExists = VoyageManifestPassengerInfo.voyageExistInDatabase(voyageManifestPassengerInfo)

      voyageExists must beTrue
    }

    "voyageExist is false when the manifests doesn't exist" in new Context {

      val voyageExists = VoyageManifestPassengerInfo.voyageExistInDatabase(voyageManifestPassengerInfo.copy(eventCode = "CI"))

      voyageExists must beFalse
    }

    "can delete an existing voyage passenger info" in new Context {
      val savedVoyageManifests1 = voyageManifestPassengerInfo
      VoyageManifestPassengerInfo.voyageExistInDatabase(savedVoyageManifests1)
      val savedVoyageManifests2 = voyageManifestPassengerInfo.copy(passengerIdentifier = Some("2"))
      VoyageManifestPassengerInfo.voyageExistInDatabase(savedVoyageManifests2)
      val savedVoyageManifests3 = voyageManifestPassengerInfo.copy(passengerIdentifier = Some("3"))
      VoyageManifestPassengerInfo.voyageExistInDatabase(savedVoyageManifests3)


      VoyageManifestPassengerInfo.deleteVoyage(voyageManifestPassengerInfo)

      val voyageExists = VoyageManifestPassengerInfo.voyageExistInDatabase(savedVoyageManifests1)
      voyageExists must beFalse

      val dbFlights = VoyageManifestPassengerInfo.flights
      dbFlights.size mustEqual 0
    }

    "will not delete an existing voyage with a different arrivalPortCode" in new Context {
      VoyageManifestPassengerInfo.batchInsert(Export.toBatch(List(voyageManifestPassengerInfo)))

      VoyageManifestPassengerInfo.deleteVoyage(voyageManifestPassengerInfo.copy(arrivalPortCode = "BHX"))


      val voyageExists = VoyageManifestPassengerInfo.voyageExistInDatabase(voyageManifestPassengerInfo)
      voyageExists must beTrue

      val dbFlights = VoyageManifestPassengerInfo.flights
      dbFlights.size mustEqual 1
    }
  }
}
