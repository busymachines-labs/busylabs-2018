package com.busymachines.labs.lab10.movieserver.dao

import com.busymachines.labs.lab10.movieserver.api.{MovieCreate, MovieDto}

import scala.concurrent.Future

trait MovieDao {
  def getAllMovies(): Future[Seq[MovieDbObject]]

  def getMovie(id: String): Future[Option[MovieDbObject]]

  def addMovie(title: String, year: Int, director: String, rating: Double): Future[String]

  def deleteMovie(Id: String): Future[String]

  def updateMovie(id: String, title: String, year: Int, director: String, rating: Double): Future[String]
}

case class MovieDbObject      (id: String, title: String, year: Int, director: String, rating: Double)

case class MovieCommentDbObject(id: String, movieId: String, userId: String, comment: String)