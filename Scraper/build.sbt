import sbt._

name := "Scraper"

version := "0.1"

scalaVersion := "2.12.6"

lazy val scalaScrapper = "net.ruippeixotog" %% "scala-scraper" % "2.1.0" withSources ()