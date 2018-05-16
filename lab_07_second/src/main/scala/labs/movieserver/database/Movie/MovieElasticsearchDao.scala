package labs.movieserver.database.Movie

import com.sksamuel.elastic4s.analyzers.StopAnalyzer
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.get.GetResponse
import com.sksamuel.elastic4s.http.{HttpClient}
//import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.ElasticsearchClientUri

import com.sksamuel.elastic4s.http.HttpExecutable
import scala.util.Random
//import labs.movieserver.datamodel.{MovieWithoutId}
import labs.movieserver.datamodel._
import spray.json.DefaultJsonProtocol._
import spray.json.{RootJsonFormat}
import spray.json._


import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, _}


class MovieElasticsearchDao(elasticClient: HttpClient, indexName: String)(implicit val ex: ExecutionContext) extends MovieDao {

  implicit val movieFormat: RootJsonFormat[MovieWithoutId] = jsonFormat3(MovieWithoutId)
  implicit val movieIdFormat  = jsonFormat4(Movie)

  val typeName: String = "movie"

//  Await.result(onStartup, 10 seconds)


  override def addMovie(movie: MovieWithoutId): Future[String] =
    for {
      movieId <- getFreeId
      movieWithId = Movie(movieId, movie.title, movie.year, movie.rating)
      _ <- elasticClient.execute {
        (indexInto (indexName, typeName) doc movieWithId.toJson.toString() id movieId)
      }
    } yield movieId

  override def deleteMovie(movieId: String): Future[String] = for {
    _ <- elasticClient.execute {
      delete(movieId) from (indexName,typeName)
    }
  } yield (movieId)

  override def getAllMovies: Future[Seq[Movie]] =
    for {
      results <- elasticClient.execute {
          search(indexName)
      }
      movies = results match {
          case Right(r) => r.result.hits.hits.map(x => {x.sourceAsString.parseJson.convertTo[Movie]}).toList
          case _ => throw new Exception("Movie database is empty!")
      }
  } yield movies

  override def getMovie(movieId: String): Future[Movie] =
    for {
      result <- elasticClient.execute {
        get(indexName,typeName,movieId)
      }
      response = result.getOrElse(throw RequestFailed())
      _ = if(response.result.isSourceEmpty || !response.result.exists) throw MovieNotFoundException(movieId)
      movie = response.result.sourceAsString.parseJson.convertTo[Movie]
    } yield movie

  override def updateMovie(movieId: String, movie: MovieWithoutId): Future[String] =
    for {
      m <- getMovie(movieId)
      newMovie = Movie(m.id, movie.title, movie.year, movie.rating)
      _ <- elasticClient.execute {
        (indexInto (indexName, typeName) doc newMovie.toJson.toString() id movieId)
      }
    } yield movieId



//  private def onStartup: Future[Unit] =
//    for {
//      _ <- elasticClient.execute{
//        mapping(typeName)        as (
//            nestedField("id"),
//            textField("title"),
//            intField("year"),
//            doubleField("rating") analyzer StopAnalyzer
//        )}
//    } yield ()





  private def getFreeId: Future[String] = Future.successful(Random.nextInt(100000).toString)
//  {
//    val newId: String = Random.nextInt(100000).toString
//    for {
//      allMovies <- getAllMovies
//      finalId <- if(allMovies.exists(_.id == newId)) getFreeId else Future.successful(newId)
//    } yield finalId
//  }

}
