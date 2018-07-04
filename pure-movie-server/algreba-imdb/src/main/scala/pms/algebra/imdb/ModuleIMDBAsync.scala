package pms.algebra.imdb

import net.ruippeixotog.scalascraper.model.Document
import pms.effects._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 25 Jun 2018
  *
  */
trait ModuleIMDBAsync[F[_]] {
  implicit def async: Async[F]

  def algebraConfig: IMDBConfig

  def imdbAlgebra: IMDBAlgebra[F] = _imdbAlgebra

  private lazy val _imdbAlgebra: IMDBAlgebra[F] = new impl.AsyncIMDBAlgebraImpl[F](algebraConfig)

}
