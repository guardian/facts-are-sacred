import sbt._

object Dependencies {

	// Versions
	val awsClientVersion = "1.11.99"

	// Dependencies
	lazy val awsDynamoDB = "com.amazonaws" % "aws-java-sdk-dynamodb" % awsClientVersion
	lazy val scanamo = "com.gu" %% "scanamo" % "0.9.2"
  lazy val capi = "com.gu" %% "content-api-client" % "7.3"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
}