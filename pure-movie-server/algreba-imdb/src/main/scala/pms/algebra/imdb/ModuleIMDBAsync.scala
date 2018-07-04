package pms.algebra.imdb
import RequestLimiter.RateLimiter
import pms.effects._
import scala.concurrent.duration._


/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleIMDBAsync[F[_]] {
  implicit def async: Async[F]


  def imdbConfig: IMDBAlgebraConfig

  val rateLimiter = RateLimiter(imdbConfig.reqTimeLimit.millis, imdbConfig.reqNumber)

  def imdbAlgebra: IMDBAlgebra[F] = _imdbAlgebra

  private lazy val _imdbAlgebra: IMDBAlgebra[F] = new impl.AsyncIMDBAlgebraImpl[F](rateLimiter)
}
