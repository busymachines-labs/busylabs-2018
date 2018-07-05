package pms.algebra.imdb.impl

import pms.effects._
import pms.algebra.imdb._

import java.time.Year
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import RequestLimiter._
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](rateLimit: RateLimiter[Nothing])(
  implicit val F: Async[F]
)
  extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {

    val browser = JsoupBrowser()

    val movieN: String = title.toString

    val result  = rateLimit{Future.successful(browser.get(s"https://www.imdb.com/search/title?title=$movieN"))}

    val doc = Await.result(result, Duration.Inf)


    val movie =
      for {
        title_el <- doc >?> element("h3 a")
        spans <- doc >?> elementList("h3 span")
        title = IMDBTitle(title_el.text)
        if(title.length > 0);
        year = {
          if (title_el.text.length != 0)
            Some(ReleaseYear(Year parse spans(1).text.substring(1, 5)))
          else
            None
        }
      } yield IMDBMovie(title, year)

    F.liftIO(IO(movie))
  }
}
