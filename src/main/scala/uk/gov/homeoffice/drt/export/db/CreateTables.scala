package uk.gov.homeoffice.drt.export.db

import scalikejdbc.AutoSession
import scalikejdbc._
import uk.gov.homeoffice.drt.export.HasConfig

class CreateTables extends HasConfig {

  val driver = config.getString("db.driver")

  implicit val session = AutoSession

  // table creation, you can run DDL by using #execute as same as JDBC

  if (driver == "org.postgresql.Driver")
  sql"""
 create table voyage_manifest (
   id serial NOT NULL,
   event_code varchar(2) NOT NULL,
   arrival_port_code varchar(5) NOT NULL,
   departure_port_code varchar(5) NOT NULL,
   voyager_number varchar(10) NOT NULL,
   carrier_code varchar(5) NOT NULL,
   scheduled_date timestamp NOT NULL,
   PRIMARY KEY (event_code, arrival_port_code, departure_port_code, voyager_number,scheduled_date),
   UNIQUE(id)
 )
""".execute.apply()
  else {
    sql"""
    create table voyage_manifest (
      id integer NOT NULL AUTO_INCREMENT,
      event_code varchar(2) NOT NULL,
      arrival_port_code varchar(5) NOT NULL,
      departure_port_code varchar(5) NOT NULL,
      voyager_number varchar(10) NOT NULL,
      carrier_code varchar(5) NOT NULL,
      scheduled_date timestamp NOT NULL,
      PRIMARY KEY (event_code, arrival_port_code, departure_port_code, voyager_number,scheduled_date)
    )
""".execute.apply()
  }

  sql"""
 create table passenger_info (
   voyage_manifest_id integer NOT NULL,
   document_type varchar(5),
   document_issuing_country_code varchar(5) NOT NULL,
   eea_flag varchar(5) NOT NULL,
   age varchar(5),
   disembarkation_port_code varchar(5),
   in_transit_flag varchar(2),
   disembarkation_port_country_code varchar(5),
   nationality_country_code varchar(5),
   passenger_identifier varchar(5),
   in_transit boolean DEFAULT FALSE NOT NULL,
   FOREIGN KEY (voyage_manifest_id) REFERENCES voyage_manifest (id)
 )
""".execute.apply()
}
