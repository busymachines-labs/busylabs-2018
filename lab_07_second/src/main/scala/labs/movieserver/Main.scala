package labs.movieserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{RejectionHandler, Route}
import akka.stream.ActorMaterializer
import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.index.admin.IndexExistsResponse
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import com.sksamuel.elastic4s.http.ElasticDsl.{indexExists, _}
import com.typesafe.config.ConfigFactory
import labs.movieserver.database.Movie.MovieElasticsearchDao
import labs.movieserver.database.MovieQuote.MovieQuoteElasticSearchDao
import labs.simplemovieserver.api.MovieApi
import com.sksamuel.elastic4s.http.ElasticDsl
import labs.movieserver.api.MovieQuoteApi
import akka.http.scaladsl.server.Directives._
import labs.movieserver.migrationService.MovieService

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main extends App {

  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher


  val elasticClient = HttpClient(ElasticsearchClientUri("localhost", 9200))
  val indexName = "lab_07"
  val conf = ConfigFactory.load

  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  (for {
    indexExists <- elasticClient.execute { indexExists(indexName) }
    _ = if (!indexExists.isRight) elasticClient.execute { createIndex(indexName) }
  } yield ()).await

  println(s"Starting Server at ${host}:${port}")

  val movieDao = new MovieElasticsearchDao(elasticClient, indexName)
  val movieQuotDao = new MovieQuoteElasticSearchDao(elasticClient, indexName)
  val movieService = new MovieService(movieDao, movieQuotDao)

  val movieApi = new MovieApi(movieDao, movieService)
  val quoteApi = new MovieQuoteApi(movieQuotDao)



  val routes: Route =  movieApi.movieRoute ~ quoteApi.movieQuoteRoute

  val bindingFuture = Http().bindAndHandle(routes, host, port)

//  Await.result(for {
//    movies <- movieService.filterMovies(3)
//  } yield movies.toList.map( m => println("Ana: " +  m)), 30 seconds)


}



