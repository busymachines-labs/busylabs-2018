package com.busymachines.labs.lab10.movieserver.service

import com.busymachines.labs.lab10.movieserver.api.{MovieCreate, MovieDto, MovieNotFoundException}
import com.busymachines.labs.lab10.movieserver.dao.{MovieDao, MovieDbObject}

import scala.concurrent.{ExecutionContext, Future}

class MovieService (movieDao: MovieDao)(implicit ec: ExecutionContext) {
    def getAllMovies(): Future[Seq[MovieDto]] =
      movieDao.getAllMovies().map(_.map(translateDbObject))

    def getMovie(id: String): Future[MovieDto] =
      for {
        movieOption <- movieDao.getMovie(id)
        movie <- movieOption match {
          case Some(m) => Future.successful(m)
          case None => Future.failed(MovieNotFoundException(id))
        }
      } yield translateDbObject(movie)

    def addMovie(newMovie: MovieCreate): Future[String] = {
      movieDao.addMovie(title = newMovie.title, year = newMovie.year,
        director = newMovie.director, rating = newMovie.rating)
    }

    def deleteMovie(id: String): Future[String] =
      for {
        _ <- getMovie(id)
        res <- movieDao.deleteMovie(id)
      } yield res

    def updateMovie(id: String, updateObject: MovieCreate): Future[String] =
      for {
        _ <- getMovie(id)
        res <- movieDao.updateMovie(id = id, title = updateObject.title, year = updateObject.year,
          director = updateObject.director, rating = updateObject.rating)
      } yield res

    private def translateDbObject(movieDbObject: MovieDbObject): MovieDto =
      MovieDto(
        id = movieDbObject.id, title = movieDbObject.title, year = movieDbObject.year,
        director = movieDbObject.director, rating = movieDbObject.rating
      )
}
