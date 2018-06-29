package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{allText, element}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._

import pms.effects._
import pms.algebra.imdb._
import java.time.Year

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
    val movie = "Star Wars"
    val browser = JsoupBrowser()
    val url = s"https://www.imdb.com/find?ref_=nv_sr_fn&q=${movie}&s=all"
    val doc2 = browser.get(url)

    val mov = for {
      main <- doc2 >?> element("#main .findList tr")
      res_text <- main >?> element(".result_text ")
      td <- res_text >?> element("td")
      title <- td >?> allText("a")
      year = td >?> text
      yearFinale = year match {
      case Some(x) => Year.parse(x.substring(x.lastIndexOf(">")))
      case None => Year.parse("0")
    }

    } yield IMDBMovie(IMDBTitle(title), Option(ReleaseYear(yearFinale)))
    F.liftIO(IO(mov))
  }
}

