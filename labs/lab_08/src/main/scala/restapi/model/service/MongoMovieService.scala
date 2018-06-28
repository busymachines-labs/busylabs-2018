package restapi.model.service

import com.mongodb.casbah.MongoConnection
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.ConfigFactory
import restapi.model.dao.{Movie, MovieWithoutId}

import scala.concurrent.{ExecutionContext, Future}

class MongoMovieService (implicit ec: ExecutionContext) extends MovieService {

  val conf = ConfigFactory.load()
  val connection = MongoConnection(conf.getString("mongo.host"))
  val collection = connection(conf.getString("mongo.database"))(conf.getString("mongo.collection"))

  override def addMovie(movie: MovieWithoutId): Future[String] = {
    val dbObject = {
      val builder = MongoDBObject.newBuilder
      builder += "title" -> movie.title
      builder += "year" -> movie.year
      builder += "rating" -> movie.rating
      builder.result
    }
    collection.save(dbObject)
    Future.successful(movie.title)
  }

  override def getAllMovies: Future[Seq[Movie]] = {
    Future.failed(new Error("not implemented"))
  }

  override def getMovie(movieId: String): Future[Movie] = {
    Future.failed(new Error("not implemented"))
  }

  override def deleteMovie(id: String): Future[String] = {
    Future.failed(new Error("not implemented"))
  }

  override def updateMovie(id: String, movie: MovieWithoutId): Future[String] = {
    Future.failed(new Error("not implemented"))
  }
}
