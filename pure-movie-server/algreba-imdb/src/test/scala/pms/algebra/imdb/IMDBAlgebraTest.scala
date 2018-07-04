package pms.algebra.imdb

import java.time.Year

import org.specs2.mutable.Specification
import org.specs2._
import cats.effect._


class IMDBAlgebraTest extends Specification {
  private val imdbConfig = IMDBConfig(100,1)
  private val imdbAlgebra = new impl.AsyncIMDBAlgebraImpl[IO](imdbConfig)

  "Tests" >> {
    "Return none" >> {
      {
        val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("bjteiovdvvvgb"))
        val resNone = None
        result.unsafeRunSync() mustEqual resNone
      }
    }
    "Star Wars" >> {
      {
        val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Star Wars"))
        val resStWars = Option(IMDBMovie(IMDBTitle("Star Wars"), Option(ReleaseYear(Year.parse("1977")))))
        result.unsafeRunSync() mustEqual resStWars
      }
    }
    "Year without god" >> {
      val result = imdbAlgebra.scrapeMovieByTitle(TitleQuery("Year without god"))
      val resY = Option(IMDBMovie(IMDBTitle("Year Without God"),None))
      result.unsafeRunSync() mustEqual resY
    }
  }
}
