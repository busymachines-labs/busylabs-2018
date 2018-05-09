package labs.simplemovieserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import labs.simplemovieserver.database.{MemoryMovieDAO, PsqlMovieDAO}
import labs.simplemovieserver.datamodel.{Movie, MovieWithoutId}
import com.typesafe.config.ConfigFactory
import labs.simplemovieserver.api.MovieApi

import scala.io.StdIn

// To run postgres, use:
//docker container run -d --name postgres-runner -p 5432:5432 -e POSTGRES_DB=mymoviedatabase -e POSTGRES_USER=busystudent -e POSTGRES_PASSWORD=qwerty postgres
//docker container exec -it postgres-runner psql -U busystudent -d mymoviedatabase

object Main extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher



  val conf = ConfigFactory.load

  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  val initialMovieTitle = conf.getString("initialmovie.title")
  val initialMovieYear = conf.getInt("initialmovie.year")
  val initialMovieRating = conf.getDouble("initialmovie.rating")

  val psqlHost = conf.getString("psql.host")
  val psqlDatabase = conf.getString("psql.database")
  val psqlUser = conf.getString("psql.user")
  val psqlPassword = conf.getString("psql.password")


  println(s"Starting Server at ${host}:${port}")
  println(s"Adding initial movie [title = ${initialMovieTitle}, year = ${initialMovieYear}, rating = ${initialMovieRating}]")

  val movieDao = new MemoryMovieDAO(MovieWithoutId(initialMovieTitle, initialMovieYear, initialMovieRating))
  //val movieDao = new PsqlMovieDAO(MovieWithoutId(initialMovieTitle, initialMovieYear, initialMovieRating),
  //  dbHost = psqlHost, dbUser = psqlUser, dbPassword = psqlPassword, dbName = psqlDatabase)
  val movieApi = new MovieApi(movieDao)

  val bindingFuture = Http().bindAndHandle(movieApi.movieRoute, host, port)

}
