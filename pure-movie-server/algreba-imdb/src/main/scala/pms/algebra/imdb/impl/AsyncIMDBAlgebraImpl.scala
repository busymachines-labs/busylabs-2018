package pms.algebra.imdb.impl

import java.time.Year

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import pms.algebra.imdb.{RateLimiter, _}
import pms.effects._
import net.ruippeixotog.scalascraper.model.Document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](
   private val config: IMDBConfig)(
   implicit val F: Async[F]

) extends IMDBAlgebra[F] {

  private val timeLimiter = RateLimiter[Document](100.millis, 1)

  def getMoviePage(title: TitleQuery): String = {
    val input = title.replace(" ", "")
    val browser = JsoupBrowser()
    val doc = browser.get(s"https://www.imdb.com/find?q=$input")
    val titleList = doc >> elementList("table tbody tr td a")
    val link = (titleList >> attr("href")).head
    link
  }

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val browser = JsoupBrowser()

    val v = Await.result(timeLimiter.apply(Future {
      browser.get(s"https://www.imdb.com${getMoviePage(title)}")
    }), 100.millis)


    val titleList2 = v >> elementList("#title-overview-widget h1 a")
    val year = Year.parse(titleList2.map(_ >> allText("a")).head)
    val imdbTitle = IMDBTitle(title)
    val imdbReleaseYear = ReleaseYear(year)
    val imdbMovie = IMDBMovie (imdbTitle, Option(imdbReleaseYear))
    F.pure(Option(imdbMovie))
  }
}
