package labs.movieserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import labs.movieserver.database.Movie.MovieElasticsearchDao
import labs.simplemovieserver.api.MovieApi

object Main extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher



  val conf = ConfigFactory.load

  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  println(s"Starting Server at ${host}:${port}")

  val movieDao = new MovieElasticsearchDao
  val movieApi = new MovieApi(movieDao)

  val bindingFuture = Http().bindAndHandle(movieApi.movieRoute, host, port)

}
