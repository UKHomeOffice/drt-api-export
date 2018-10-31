package uk.gov.homeoffice.drt.export.db

import scalikejdbc.{AutoSession, ConnectionPool}
import scalikejdbc._
import uk.gov.homeoffice.drt.export.HasConfig

trait Database extends HasConfig {
  val driver = config.getString("db.driver")
  val url = config.getString("db.url")
  val user = config.getString("db.user")
  val password = config.getString("db.password")

  // initialize JDBC driver & connection pool
  Class.forName(driver)
  ConnectionPool.singleton(url, user, password)
  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}

