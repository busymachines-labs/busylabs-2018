package pms.algebra.imdb


import pms.config.ConfigLoader
import pms.effects.Sync

final case class IMDBConfig(
                           durationInterval:Long,
                           maxRequests:Int
                           )

object IMDBConfig extends ConfigLoader[IMDBConfig] {
  override def default[F[_]: Sync]: F[IMDBConfig] =
    this.load[F]("pms.algebra")
}


