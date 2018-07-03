package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pms.effects._
import pms.algebra.imdb._
import java.time.Year

import net.ruippeixotog.scalascraper.model.Document
import pms.algebra.imdb.extra.RateLimiter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](val rateLimiter : RateLimiter[Document])(
  implicit val F: Async[F]
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val browser = JsoupBrowser()
    val imdbScrapeRequest = rateLimiter.addToQueue(Future{
      browser.get(s"https://imdb.com/find?q=$title&s=tt")
    })
    val doc = Await.result(imdbScrapeRequest, Duration.Inf)

    val movie = for {
      findList <- doc tryExtract elementList(".findList tr")
      firstElement <- findList.headOption
      resultText <- firstElement tryExtract element(".result_text")
      titleElement <- resultText tryExtract element("a")
      title = IMDBTitle(titleElement.text)
      resultTextStr = resultText.text
      yearStartPos = resultTextStr.indexOf("(")
      year = if(yearStartPos > 0) Option(ReleaseYear(Year.parse(resultTextStr.substring(yearStartPos + 1, yearStartPos + 5))))
              else None
    } yield IMDBMovie(title, year)
    F.liftIO(IO(movie))
  }
}
