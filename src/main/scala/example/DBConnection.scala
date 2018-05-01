package example

import slick.jdbc.H2Profile.backend.Database

object DBConnection extends App {
  val db = Database.forConfig("mysql")
  db.createSession()
}