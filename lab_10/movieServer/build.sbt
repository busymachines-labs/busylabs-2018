lazy val root = Project("movieServer", file("."))
  .settings(
    name         := "movieServer",
    version      := "0.1",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "org.flywaydb"      % "flyway-core"           % "4.2.0",
      "com.typesafe"      % "config"                % "1.3.2",
      "org.postgresql"    % "postgresql"            % "42.1.4",
      "com.typesafe.akka" %% "akka-http"            % "10.1.1",
      "com.typesafe.akka" %% "akka-stream"          % "2.5.11",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
      "org.tpolecat"      %% "doobie-core"          % "0.5.3"
    )
  )
  .dependsOn(email)
  .aggregate(email)

//go two level ups
lazy val email = ProjectRef(file("../../email"), "email")
