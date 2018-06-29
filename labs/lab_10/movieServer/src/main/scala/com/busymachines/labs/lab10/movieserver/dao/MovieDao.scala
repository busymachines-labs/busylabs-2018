package com.busymachines.labs.lab10.movieserver.dao

import busymachines.effects._

trait MovieDao {
  def getAllMovies(): IO[Seq[MovieDbObject]]

  def getMovie(id: String): IO[Option[MovieDbObject]]

  def addMovie(title: String, year: Int, director: String, rating: Double): IO[String]

  def deleteMovie(Id: String): IO[String]

  def updateMovie(id: String, title: String, year: Int, director: String, rating: Double): IO[String]
}

case class MovieDbObject(id: String, title: String, year: Int, director: String, rating: Double)

case class MovieCommentDbObject(id: String, movieId: String, userId: String, comment: String)
