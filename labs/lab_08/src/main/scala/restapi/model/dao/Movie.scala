package restapi.model.dao


case class Movie(id: String, title: String, year: Int, rating: Double)

case class MovieWithoutId(title: String, year: Int, rating: Double)

case class MovieNotFoundException(movieId: String) extends Exception(s"Not found movie with Id ${movieId}")
