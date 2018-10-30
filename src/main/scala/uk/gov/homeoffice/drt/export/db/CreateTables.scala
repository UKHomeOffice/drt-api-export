package uk.gov.homeoffice.drt.export.db

import scalikejdbc.AutoSession

import scalikejdbc._

class CreateTables {

  implicit val session = AutoSession

  // table creation, you can run DDL by using #execute as same as JDBC
  sql"""
 create table flights (
   FLIGHTS_ID integer NOT NULL AUTO_INCREMENT,
   EVENT_CODE varchar(2) NOT NULL,
   ARRIVAL_PORT_CODE varchar(5) NOT NULL,
   DEPARTURE_PORT_CODE varchar(5) NOT NULL,
   VOYAGER_NUMBER varchar(10) NOT NULL,
   CARRIER_CODE varchar(5) NOT NULL,
   SCHEDULED_DATE timestamp NOT NULL,
   PRIMARY KEY (ARRIVAL_PORT_CODE, CARRIER_CODE, VOYAGER_NUMBER, SCHEDULED_DATE)
 )
""".execute.apply()

  sql"""
 create table passengers (
   FLIGHTS_ID integer NOT NULL,
   DOCUMENT_TYPE varchar(5),
   COUNTRY_CODE varchar(5) NOT NULL,
   AGE varchar(5),
   FOREIGN KEY (FLIGHTS_ID) REFERENCES flights (FLIGHTS_ID)
 )
""".execute.apply()
}
