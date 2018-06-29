package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import pms.effects._
import pms.algebra.imdb._
import java.time.Year

import cats.effect.IO.timer
import cats.effect.Timer

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](
  implicit val F: Async[F]
) extends IMDBAlgebra[F] {

  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val doc = JsoupBrowser().get(s"https://imdb.com/find?q=$title&s=tt")

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
