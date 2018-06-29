package labs.movieserver.database.MovieQuote

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.analyzers.StopAnalyzer
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.ElasticDsl
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.HttpExecutable
import labs.movieserver.datamodel._
import spray.json.RootJsonFormat

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.Random
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import spray.json._

class MovieQuoteElasticSearchDao(elasticClient: HttpClient, indexName: String)(implicit val ex: ExecutionContext) extends MoviQuoteDao {

  implicit val movieFormat: RootJsonFormat[MovieQuoteWithoutId] = jsonFormat2(MovieQuoteWithoutId)
  implicit val movieIdFormat  = jsonFormat3(MovieQuote)

  val typeName: String = "moviequote"

  override def addMovieQuote(quote: MovieQuoteWithoutId): Future[String] =
  for {
      quoteId <- getFreeId
      movieWithId = MovieQuote(quoteId, quote.movieId, quote.quote)
      _ <- elasticClient.execute {
      (indexInto (indexName, typeName) doc movieWithId.toJson.toString() id quoteId)
      }
  } yield quoteId


  override def getMovieQuote(quote: String): Future[MovieQuote] = ???
//    for {
//      result <- elasticClient.execute {
//        search(indexName) query matchAllQuery() postFilter termQuery("quote", quote)
//      }
//
//      response = result.getOrElse(throw RequestFailed())
//      _ = if(response.result.isSourceEmpty || !response.result.exists) throw MovieQuoteNotFoundException(movieId)
//      quote = response.result.sourceAsString.parseJson.convertTo[MovieQuote]
//
//  } yield quote

  override def deleteMovieQuote(quoteId: String): Future[String] = ???

  override def updateMovieQuote(movieId: String, quote: MovieQuoteWithoutId): Future[String] = ???

  private def getFreeId: Future[String] = Future.successful(Random.nextInt(100000).toString)


}
