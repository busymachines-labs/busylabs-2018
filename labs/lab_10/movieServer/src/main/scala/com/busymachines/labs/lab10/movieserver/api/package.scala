package com.busymachines.labs.lab10.movieserver

package object api {
  case class MovieDto(id: String, title: String, year: Int, director: String, rating: Double)
  case class MovieCreate(title: String, year: Int, director: String, rating: Double)

  case class MovieNotFoundException(movieId: String) extends Exception(s"Not found movie with Id ${movieId}")
}
