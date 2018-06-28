package labs.movieserver.migrationService

import labs.movieserver.database.Movie.MovieDao
import labs.movieserver.datamodel.{Movie, MovieQuote, MovieWithoutId}
import labs.movieserver.database.MovieQuote.MoviQuoteDao

import scala.concurrent.{ExecutionContext, Future}

class MovieService(movieDao: MovieDao, quoteMovieDao: MoviQuoteDao)(implicit ex: ExecutionContext) {

    def filterMovies(rating: Double): Future[Seq[Movie]] =
        for {
          movies <- movieDao.getAllMovies
          filteredMovies = movies.filter(m => m.rating > rating)
          _ <- iterativeFuture(filteredMovies.toList.map(movie => () => {
              for{
               _ <- movieDao.updateMovie(movie.id, MovieWithoutId(movie.title, movie.year, movie.rating+2))
              } yield()
          })).map(_ => ())

          newMovies <- movieDao.getAllMovies
        } yield newMovies

    def updateQuotes(newQuote: String): Future[Seq[MovieQuote]] = ???


    private def iterativeFuture[T](futureSeq: List[() => Future[T]]): Future[Seq[T]] = futureExtraction(futureSeq)(Nil)

    private def futureExtraction[T](values: List[() => Future[T]])(soFar: List[T]): Future[List[T]] =
        values match {
            case Nil => Future.successful(Nil)
            case finalValue :: Nil => finalValue().map(x => (x :: soFar).reverse)
            case intermediaryValue :: restOfValues => intermediaryValue().flatMap(x => futureExtraction(restOfValues)(x::soFar))
        }
}
