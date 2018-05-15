package labs.movieserver.datamodel

case class Movie(id: String, title: String, year: Int, rating: Double)

case class MovieWithoutId(title: String, year: Int, rating: Double)

case class MovieNotFoundException(movieId: String) extends Exception(s"Not found movie with Id ${movieId}")



case class User(id: String, name: String, email: String )

case class UserId(title: String, name: String, email: String)

case class UserNotFoundException(userId: String) extends Exception(s"Not found User with Id ${userId}")
