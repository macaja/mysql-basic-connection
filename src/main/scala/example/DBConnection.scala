package example

import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

/*
  Example Data Model

  +------------------+
  | COMPUTER         |
  +------------------+        +---------+
  | ID               |        | COMPANY |
  | NAME             |        +---------+
  | MANUFACTURER_ID--+--------| ID      |
  +------------------+        | NAME    |
                              +---------+
*/
// result types
case class Computer(id: Int, name: String, manufacturerId: Int)
case class Company(id: Int, name: String)

// schema description
class Computers(tag: Tag) extends Table[Computer](tag, "COMPUTER") {
  def id = column[Int]("ID")
  def name = column[String]("NAME")
  def manufacturerId = column[Int]("MANUFACTURER_ID")
  def * = (id, name, manufacturerId) <> (Computer.tupled, Computer.unapply)
}
class Companies(tag: Tag) extends Table[Company](tag, "COMPANY") {
  def id = column[Int]("ID")
  def name = column[String]("NAME")
  def * = (id, name) <> (Company.tupled, Company.unapply)
}

object DBConnection extends App {

  def Computers = TableQuery[Computers]
  def Companies = TableQuery[Companies]

  //val db = Database.forConfig("mysql")
  val db = Database.forURL(url = "jdbc:mysql://localhost:3306/test", user = "root", password = "",driver = "com.mysql.jdbc.Driver", keepAliveConnection = true)

  val setup = DBIO.seq(
    Companies.schema.create,
    Computers.schema.create,
    Companies += Company((1), "Apple Inc."),
    Companies += Company((2), "Thinking Machines"),
    Companies += Company((3), "RCA"),
    Computers += Computer((1), "MacBook Pro 15.4 inch", (1)),
    Computers += Computer((2), "CM-2a", (2)),
    Computers += Computer((3), "CM-200", (2)),
  )

  def runQuery = {
    val queryFuture = Future {
      //A very naive query which is the equivalent of SELECT * FROM TABLE
      //and having the FRM map the columns to the params of a partial function
      //
      db.run(Companies.result).map(_.foreach {
        case e => println(s"comapny name => ${e.name}")
      })
    }

    //Everything runs asynchronously. Failure to wait for results
    //usually leads to no results :)
    //NOTE: Await does not block here!
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) =>  {
        Companies.schema.drop
        Computers.schema.drop
        db.close()  //cleanup DB connection
      }
      case Failure(err) => println(err); println("Oh Noes!")  //handy for debugging failure
    }
  }
  def doSomething() = {

    //do a drop followed by create
    val setupFuture =  Future {
      db.run(setup)
    }

    //once our DB has finished initializing we are ready to roll !
    //NOTE: Await does not block here!
    Await.result(setupFuture, Duration.Inf).andThen{
      case Success(_) => runQuery
      case Failure(err) => println(err);
    }

    //Printing this just for fun. Keep an eye on your console to see this print
    // before the query results :)
    println("Seeya!")
  }
  doSomething()

}