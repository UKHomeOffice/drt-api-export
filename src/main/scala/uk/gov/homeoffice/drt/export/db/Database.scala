package uk.gov.homeoffice.drt.export.db

import scalikejdbc.{AutoSession, ConnectionPool}
import uk.gov.homeoffice.drt.export.HasConfig

trait Database extends HasConfig {
  lazy val driver = config.getString("db.driver")
  lazy val url = config.getString("db.url")
  lazy val user = config.getString("db.user")
  lazy val password = config.getString("db.password")

  // initialize JDBC driver & connection pool
  Class.forName(driver)
  ConnectionPool.singleton(url, user, password)
  // ad-hoc session provider on the REPL
  implicit val session = AutoSession

}

