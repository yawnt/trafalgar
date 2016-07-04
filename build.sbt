name := "trafalgar"

version := "0.1-SNAPSHOT"

organization := "eu.unicredit"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-language:postfixOps",
  "-language:implicitConversions"
)

libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.7"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
