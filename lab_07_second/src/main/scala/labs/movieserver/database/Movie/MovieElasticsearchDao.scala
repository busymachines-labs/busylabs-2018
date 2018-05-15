package labs.movieserver.database.Movie

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.analyzers.StopAnalyzer
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.HttpClient

import scala.util.Random
//import labs.movieserver.datamodel.{MovieWithoutId}
import labs.movieserver.datamodel._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, _}

class MovieElasticsearchDao(implicit val ex: ExecutionContext) extends MovieDao {

  implicit val movieFormat  = jsonFormat3(MovieWithoutId)

  val elasticClient = HttpClient(ElasticsearchClientUri("localhost", 9202))
  val indexName = "lab_07"
  val typeName: String = "movie"

  Await.result(onStartup, 10 seconds)


  override def addMovie(movie: MovieWithoutId): Future[String] =
    for {
      movieId <- getFreeId
      _ <- elasticClient.execute {
        (indexInto (indexName, typeName) doc movieFormat.write(movie).toJson.toString() id movieId)
      }
    } yield movieId

  override def deleteMovie(id: String): Future[String] = ???

  override def getAllMovies: Future[Seq[Movie]] = ???

  override def getMovie(movieId: String): Future[Movie] = ???

  override def updateMovie(id: String, movie: MovieWithoutId): Future[String] = ???



  private def onStartup: Future[Unit] =
    for {
      indexExists <- elasticClient.execute { indexExists(indexName) }
      _ <- if (indexExists.isRight) Future.successful(())
      else elasticClient.execute {
              createIndex(indexName).mappings(
                mapping(typeName) as (
                  keywordField("id"),
                  textField("title"),
                  intField("year"),
                  doubleField("rating") analyzer StopAnalyzer
                )
              )
           }
    } yield ()


  private def getFreeId: Future[String] = Future.successful(Random.nextInt(100000).toString)
  //{
//    val newId: String = Random.nextInt(100000).toString
//    for {
//      allMovies <- getAllMovies
//      finalId <- if(allMovies.exists(_.id == newId)) getFreeId else Future.successful(newId)
//    } yield finalId
//  }

}
