name := "life-tracker"

version := "1.0"

scalaVersion := "2.12.2"

val elastic4sVersion = "5.4.3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.3"
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-tcp" % elastic4sVersion
libraryDependencies += "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.8.2"