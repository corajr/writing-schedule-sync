name := "writing-schedule-sync"

version := "0.0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.google.api-client" % "google-api-client" % "1.22.0",
  "com.google.oauth-client" % "google-oauth-client-jetty" % "1.22.0",
  "com.google.apis" % "google-api-services-calendar" % "v3-rev208-1.22.0",
  "org.seleniumhq.selenium" % "selenium-java" % "3.0.0-beta2",
  "com.machinepublishers" % "jbrowserdriver" % "0.16.4",
  "org.scalatest" %% "scalatest" % "3.0.0"
)
