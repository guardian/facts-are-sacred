import Dependencies._

name := "HITA"

version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  scanamo,
  awsDynamoDB,
  filters
)

addCommandAlias("devrun", "run -Dconfig.resource=DEV.conf 9990")