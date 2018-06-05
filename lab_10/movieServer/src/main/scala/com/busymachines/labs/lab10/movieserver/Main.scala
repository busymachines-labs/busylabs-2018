package com.busymachines.labs.lab10.movieserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.busymachines.labs.lab10.movieserver.api.MovieApi
import com.busymachines.labs.lab10.movieserver.dao.{MovieDaoPostgres, UserDaoPostgres}
import com.busymachines.labs.lab10.movieserver.service.MovieService
import com.typesafe.config.ConfigFactory
import org.flywaydb.core.Flyway

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val conf = ConfigFactory.load

  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  val psqlHost = conf.getString("psql.host")
  val psqlDatabase = conf.getString("psql.database")
  val psqlURL = s"jdbc:postgresql://${psqlHost}/${psqlDatabase}"

  val psqlUser = conf.getString("psql.user")
  val psqlPassword = conf.getString("psql.password")

  {

    val flyway = new Flyway()
    flyway.setDataSource(psqlURL, psqlUser, psqlPassword)
    flyway.migrate()
  }

  val userDao = new UserDaoPostgres(psqlURL = psqlURL, psqlUser = psqlUser, psqlPassword = psqlPassword)
  val movieDao = new MovieDaoPostgres(psqlURL = psqlURL, psqlUser = psqlUser, psqlPassword = psqlPassword)

  //println(s"All Users are: ${Await.result(userDao.getAllUsers, 10 seconds)}")
  //println(s"All Movies are: ${Await.result(movieDao.getAllMoviesWithComments, 10 seconds)}")

  val movieService = new MovieService(movieDao = movieDao)
  val movieApi = new MovieApi(movieService)

  println(s"Starting Server at ${host}:${port}")

  val bindingFuture = Http().bindAndHandle(movieApi.movieRoute, host, port)

}

