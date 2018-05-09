import sbt._

object Dependencies {
  lazy val slick = "com.typesafe.slick" %% "slick" % "3.2.3"
  lazy val slickHikaricp =   "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
  lazy val mysql  = "mysql" % "mysql-connector-java" % "5.1.34"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val slf4j =   "org.slf4j" % "slf4j-nop" % "1.6.4"

}
