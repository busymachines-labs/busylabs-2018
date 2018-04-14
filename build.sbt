import sbt._

lazy val root =
  Project(id = "busylabs", base = file("."))
  .aggregate(
    lab_02
  )

//equivalent to: Project(id = "lab_02", base = file("./lab_02"))
lazy val lab_02 = project.settings(
  // libraryDependencies += "org.typelevel" %% "cats" % "1.1.0",
  // libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  libraryDependencies ++= List(
    "org.typelevel" %% "cats-core" % "1.1.0",
    "org.typelevel" %% "cats-effect" % "0.10",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "com.busymachines" %% "busymachines-commons-effects" % "0.3.0-RC7"
  )
)
