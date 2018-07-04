package pms.algebra.imdb.impl

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{allText, element}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import pms.effects._
import pms.algebra.imdb._
import java.time.Year


import net.ruippeixotog.scalascraper.model.Document
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */



final private[imdb] class AsyncIMDBAlgebraImpl[F[_]](private val imdbConfig: IMDBConfig)(
  implicit val F: Async[F]
) extends IMDBAlgebra[F] {

  val limitRequest:LimitRequest[Document] = new LimitRequest[Document](0.millis, imdbConfig.maxRequests)
  override def scrapeMovieByTitle(title: TitleQuery): F[Option[IMDBMovie]] = {
    val movie = TitleQuery.exorcise(title)

    val browser = JsoupBrowser()
    val url = s"https://www.imdb.com/find?ref_=nv_sr_fn&q=${movie}&s=all"
    //val doc2 = browser.get(url)
    val v = Await.result(limitRequest.apply(Future{
      browser.get(url)
    }), imdbConfig.durationInterval.millis)
    val mov = for {
      main <- v>?>element(".findList .result_text i") match {
        case Some(x) => Some(x)
        case None => v >?>element(".findList .result_text a")
      }
      title =  IMDBTitle((main >> allText).filter(!"\"".contains(_)))
      main2 <- v>?>element(".findList .result_text")
      year = main2 >?> allText match{
        case Some(x) => if(x.equals(title)) None else Some(ReleaseYear(Year.parse(x.substring(x.indexOf("(")+1, x.lastIndexOf(")")))))
        case None => None
      }
    } yield IMDBMovie(title, year)
    F.liftIO(IO(mov))
  }
}
