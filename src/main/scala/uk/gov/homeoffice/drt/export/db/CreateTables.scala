package uk.gov.homeoffice.drt.export.db

import scalikejdbc.AutoSession
import scalikejdbc._

class CreateTables {


  implicit val session = AutoSession

  // table creation, you can run DDL by using #execute as same as JDBC

  sql"""
 create table voyage_manifest_passenger_info (
   event_code varchar(2) NOT NULL,
   arrival_port_code varchar(5) NOT NULL,
   departure_port_code varchar(5) NOT NULL,
   voyager_number varchar(10) NOT NULL,
   carrier_code varchar(5) NOT NULL,
   scheduled_date timestamp NOT NULL,
   document_type varchar(20),
   document_issuing_country_code varchar(5) NOT NULL,
   eea_flag varchar(5) NOT NULL,
   age varchar(5),
   disembarkation_port_code varchar(5),
   in_transit_flag varchar(2),
   disembarkation_port_country_code varchar(5),
   nationality_country_code varchar(5),
   passenger_identifier varchar(25),
   in_transit boolean DEFAULT FALSE NOT NULL
 )
""".execute.apply()

  sql"""
      create index idx_voyage_manifest ON voyage_manifest_passenger_info(event_code, arrival_port_code, departure_port_code, voyager_number, scheduled_date)
    """.execute().apply()

  sql"""
      create table processed_zips (
        name varchar(35) NOT NULL PRIMARY KEY
      )
    """.execute().apply()
}
