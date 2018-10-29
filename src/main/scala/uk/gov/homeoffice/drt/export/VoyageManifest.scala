package uk.gov.homeoffice.drt.export

import org.joda.time.DateTime
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.Try


case class VoyageManifest(EventCode: String,
                          ArrivalPortCode: String,
                          DeparturePortCode: String,
                          VoyageNumber: String,
                          CarrierCode: String,
                          ScheduledDateOfArrival: String,
                          ScheduledTimeOfArrival: String,
                          PassengerList: List[PassengerInfoJson]) {
  def flightCode: String = CarrierCode + VoyageNumber

  def scheduleArrivalDateTime: Option[DateTime] = Try(DateTime.parse(scheduleDateTimeString)).toOption

  def passengerInfos: Seq[PassengerInfo] = PassengerList.map(_.toPassengerInfo)

  def scheduleDateTimeString: String = s"${ScheduledDateOfArrival}T${ScheduledTimeOfArrival}Z"

  def millis: Long = scheduleArrivalDateTime.map(_.getMillis).getOrElse(0L)

  def summary: String = s"$DeparturePortCode->$ArrivalPortCode/$CarrierCode$VoyageNumber@$scheduleDateTimeString"

  def key: Int = s"$VoyageNumber-${scheduleArrivalDateTime.map(_.getMillis).getOrElse(0L)}".hashCode
}

case class PassengerInfo(DocumentType: Option[String],
                         DocumentIssuingCountryCode: String, Age: Option[Int] = None)

case class PassengerInfoJson(DocumentType: Option[String],
                             DocumentIssuingCountryCode: String,
                             EEAFlag: String,
                             Age: Option[String] = None,
                             DisembarkationPortCode: Option[String],
                             InTransitFlag: String = "N",
                             DisembarkationPortCountryCode: Option[String] = None,
                             NationalityCountryCode: Option[String] = None,
                             PassengerIdentifier: Option[String]
                            ) {
  def toPassengerInfo = PassengerInfo(DocumentType, DocumentIssuingCountryCode, Age match {
    case Some(age) => Try(age.toInt).toOption
    case None => None
  })
}





object FlightPassengerInfoProtocol extends DefaultJsonProtocol {
  implicit val passengerInfoConverter: RootJsonFormat[PassengerInfoJson] = jsonFormat(PassengerInfoJson,
    "DocumentType",
    "DocumentIssuingCountryCode",
    "NationalityCountryEEAFlag",
    "Age",
    "DisembarkationPortCode",
    "InTransitFlag",
    "DisembarkationPortCountryCode",
    "NationalityCountryCode",
    "PassengerIdentifier"
  )
  implicit val passengerInfoResponseConverter: RootJsonFormat[VoyageManifest] = jsonFormat8(VoyageManifest)
}
