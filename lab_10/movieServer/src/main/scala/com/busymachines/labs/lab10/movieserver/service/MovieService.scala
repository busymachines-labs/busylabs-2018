package com.busymachines.labs.lab10.movieserver.service

import com.busymachines.labs.lab10.movieserver.api.{MovieCreate, MovieDto, MovieNotFoundException}
import com.busymachines.labs.lab10.movieserver.dao.{MovieDao,    MovieDbObject}

import busymachines.effects._

class MovieService(movieDao: MovieDao) {

  def getAllMovies(): IO[Seq[MovieDto]] =
    movieDao.getAllMovies().map(_.map(translateDbObject))

  def getMovie(id: String): IO[MovieDto] =
    for {
      movieOption <- movieDao.getMovie(id)
      movie <- movieOption match {
                case Some(m) => IO.pure(m)
                case None    => IO.raiseError(MovieNotFoundException(id))
              }
    } yield translateDbObject(movie)

  def addMovie(newMovie: MovieCreate): IO[String] = {
    movieDao
      .addMovie(title = newMovie.title, year = newMovie.year, director = newMovie.director, rating = newMovie.rating)
  }

  def deleteMovie(id: String): IO[String] =
    for {
      _   <- getMovie(id)
      res <- movieDao.deleteMovie(id)
    } yield res

  def updateMovie(id: String, updateObject: MovieCreate): IO[String] =
    for {
      _ <- getMovie(id)
      res <- movieDao.updateMovie(
              id       = id,
              title    = updateObject.title,
              year     = updateObject.year,
              director = updateObject.director,
              rating   = updateObject.rating
            )
    } yield res

  private def translateDbObject(movieDbObject: MovieDbObject): MovieDto =
    MovieDto(
      id       = movieDbObject.id,
      title    = movieDbObject.title,
      year     = movieDbObject.year,
      director = movieDbObject.director,
      rating   = movieDbObject.rating
    )
}
