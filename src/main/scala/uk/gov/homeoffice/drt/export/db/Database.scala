package uk.gov.homeoffice.drt.export.db

import scalikejdbc.{AutoSession, ConnectionPool}
import scalikejdbc._

class Database(className: String, url: String, user: String, password: String) {
  // initialize JDBC driver & connection pool
  Class.forName(className)
  ConnectionPool.singleton(url, user, password)
  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}

