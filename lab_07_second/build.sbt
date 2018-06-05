name := "lab_07_second"

version := "0.1"

scalaVersion := "2.12.6"

val elastic4sVersion = "6.1.0"

allDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.2",
  "com.typesafe.akka" %% "akka-http"   % "10.1.1",
  "com.typesafe.akka" %% "akka-stream" % "2.5.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.zaxxer" % "HikariCP" % "3.1.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-slick" % "0.18",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "io.spray" %% "spray-json" % "1.3.3"
)