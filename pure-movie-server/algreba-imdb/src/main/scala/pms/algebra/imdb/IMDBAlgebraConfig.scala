package pms.algebra.imdb

import pms.config._
import pms.effects.Sync


final case class IMDBAlgebraConfig(
                                    reqNumber: Int,
                                    reqTimeLimit: Long
                                  )

object IMDBAlgebraConfig extends ConfigLoader[IMDBAlgebraConfig] {
  override def default [F[_]: Sync]: F[IMDBAlgebraConfig] =
    this.load[F]("algebra.imdb")
}
