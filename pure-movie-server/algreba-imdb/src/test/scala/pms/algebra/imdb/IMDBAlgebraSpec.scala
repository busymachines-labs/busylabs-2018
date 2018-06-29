package pms.algebra.imdb

import cats.effect.IO
import java.time.Year

class IMDBAlgebraSpec extends org.specs2.mutable.Specification {

  private val imdbAlgebra = new impl.AsyncIMDBAlgebraImpl[IO]()

  "Searching for" >> {
    "Inception must return ('Începutul', 2010)" >> {
      val inceptionMovie = Option(IMDBMovie(IMDBTitle("Începutul"), Option(ReleaseYear(Year.of(2010)))))
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Inception"))
      result.unsafeRunSync() mustEqual inceptionMovie
    }

    "Die Hard must return ('Greu de ucis', 1988)" >> {
      val dieHardMovie = Option(IMDBMovie(IMDBTitle("Greu de ucis"), Option(ReleaseYear(Year.of(1988)))))
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Die Hard"))
      result.unsafeRunSync() mustEqual dieHardMovie
    }

    "Year Without God must return ('Year Without God', None)" >> {
      val movieWithoutYear = Option(IMDBMovie(IMDBTitle("Year Without God"), None))
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Year Without God"))
      result.unsafeRunSync() mustEqual movieWithoutYear
    }

    "asdhashda must return None" >> {
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("asdhashda"))
      result.unsafeRunSync() mustEqual None
    }
  }
}
