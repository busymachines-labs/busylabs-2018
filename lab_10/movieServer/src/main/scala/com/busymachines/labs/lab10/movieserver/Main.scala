package com.busymachines.labs.lab10.movieserver

import akka.actor.ActorSystem

import akka.stream.ActorMaterializer
import com.busymachines.labs.lab10.movieserver.api.MovieApi
import com.busymachines.labs.lab10.movieserver.dao._
import com.busymachines.labs.lab10.movieserver.service.MovieService
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

import busymachines.effects._
import busymachines.rest._

object Main extends App {

  implicit val system           = ActorSystem("my-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = ExecutionContext.global

  val runIO = for {
    conf         <- IO(ConfigFactory.load)
    host         <- IO(conf.getString("server.host"))
    port         <- IO(conf.getInt("server.port"))
    psqlHost     <- IO(conf.getString("psql.host"))
    psqlDatabase <- IO(conf.getString("psql.database"))
    psqlURL      <- IO(s"jdbc:postgresql://${psqlHost}/${psqlDatabase}")

    psqlUser     <- IO(conf.getString("psql.user"))
    psqlPassword <- IO(conf.getString("psql.password"))

    userDao  = new UserDaoPostgres(psqlURL  = psqlURL, psqlUser = psqlUser, psqlPassword = psqlPassword)
    movieDao = new MovieDaoPostgres(psqlURL = psqlURL, psqlUser = psqlUser, psqlPassword = psqlPassword)

    movieService = new MovieService(movieDao = movieDao)
    movieApi     = new MovieApi(movieService)

    _ <- IO {
          val flyway = new Flyway()
          flyway.setDataSource(psqlURL, psqlUser, psqlPassword)
          flyway.migrate()
        }
    
    _ <- Http().bindAndHandle(movieApi.movieRoute, host, port).asIO
  } yield ()

  runIO.unsafeRunSync()
}
