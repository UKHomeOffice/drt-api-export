package uk.gov.homeoffice.drt.export

import com.typesafe.config.ConfigFactory

trait HasConfig {
  implicit val config = ConfigFactory.load
}
