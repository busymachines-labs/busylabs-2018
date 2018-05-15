package labs.movieserver.database.MovieQuote

import labs.movieserver.datamodel.{MovieQuote, MovieQuoteWithoutId}

import scala.concurrent.Future

trait MoviQuoteDao {

  def addMovieQuote(quote: MovieQuoteWithoutId): Future[String]

  def getMovieQuote(quote: String): Future[MovieQuote]

  def deleteMovieQuote(quoteId: String): Future[String]

  def updateMovieQuote(movieId: String, quote: MovieQuoteWithoutId): Future[String]

}
