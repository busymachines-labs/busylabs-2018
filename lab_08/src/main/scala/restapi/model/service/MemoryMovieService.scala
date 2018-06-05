package restapi.model.service

import restapi.model.dao.{Movie, MovieNotFoundException, MovieWithoutId}
import restapi.model.dao.{MovieNotFoundException, MovieWithoutId}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random


class MemoryMovieService(initialMovie: MovieWithoutId)(implicit ec: ExecutionContext) extends MovieService {
  val movieDb:mutable.HashMap[String, Movie] = mutable.HashMap[String, Movie]()
  addMovie(initialMovie)

  def getAllMovies(): Future[Seq[Movie]] = {
    println("Executing getAllMovies() on psql")
    Future(movieDb.values.toSeq)
  }

  def getMovie(movieId: String): Future[Movie] = {
    println(s"Executing getMovie(${movieId}) on psql")
    Future{movieDb.get(movieId).getOrElse(throw MovieNotFoundException(movieId))}
  }

  def addMovie(movie: MovieWithoutId): Future[String] = {
    println(s"Executing addMovie(${movie}) on psql")
    val newId: String = getFreeId
    movieDb.put(newId, new Movie(id = newId, title = movie.title, year = movie.year, rating = movie.rating))
    Future.successful(newId)
  }

  def deleteMovie(id: String): Future[String] = {
    println(s"Executing deleteMovie(${id}) on psql")
    movieDb.remove(id)
    Future.successful(id)
  }

  def updateMovie(id: String, movie: MovieWithoutId): Future[String] = {
    println(s"Executing updateMovie(${id}, ${movie}) on psql")
    movieDb.put(id, new Movie(id = id, title = movie.title, year = movie.year, rating = movie.rating))
    Future.successful(id)
  }

  private def getFreeId: String = {
    val newId: String = Random.nextInt(100000).toString
    if(movieDb.keySet.exists(_ == newId)) getFreeId
    else newId
  }
}
