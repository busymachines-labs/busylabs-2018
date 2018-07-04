package pms.algebra.imdb.impl

import cats.effect.IO
import org.specs2.mutable.Specification
import pms.algebra.imdb.IMDBMovie
import pms.algebra.imdb.impl.AsyncIMDBAlgebraImpl
import java.time
import java.time.Year

import RequestLimiter.RateLimiter
import pms.algebra.imdb.IMDBTitle
import pms.algebra.imdb.ReleaseYear
import pms.algebra.imdb.TitleQuery
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._



class AsyncIMDBAlgebraImplTest extends Specification {

  val imdbAlgebra = new AsyncIMDBAlgebraImpl[IO](rateLimit = RateLimiter(100.millis, 1))



  "Searching" >> {

    "asdasfasf should return none" >> {
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("dasddsadasdasdasdasd"))
      result.unsafeRunSync() mustEqual None
    }

    "Inception must return Începutul 2010" >> {
      val inceptionMovie = Option(IMDBMovie(IMDBTitle("Începutul"), Option(ReleaseYear(Year parse "2010"))))
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception"))
      result.unsafeRunSync() mustEqual inceptionMovie
    }

    "Lord of the Rings must return Stapînul inelelor: Fratia inelului 2001" >> {
      val lotrMovie = Option(IMDBMovie(IMDBTitle("Stapînul inelelor: Fratia inelului"), Option(ReleaseYear(Year parse "2001"))))
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Lord of the Rings"))
      result.unsafeRunSync() mustEqual lotrMovie
    }

  }
}
