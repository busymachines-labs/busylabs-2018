package restapi.model.service


import restapi.model.dao.{Movie, MovieWithoutId}

import scala.concurrent.Future

trait MovieService {

  def getAllMovies: Future[Seq[Movie]]

  def getMovie(movieId: String): Future[Movie]

  def addMovie(movie: MovieWithoutId): Future[String]

  def deleteMovie(id: String): Future[String]

  def updateMovie(id: String, movie: MovieWithoutId): Future[String]
}
