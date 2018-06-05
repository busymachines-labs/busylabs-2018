package labs.sec.jwt

import cats.effect.Sync

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
trait JWTService[F[_]] {

  //TODO: add custom payload
  def issue(secret: JWTSecret): F[JWT]

  def verify(secret: JWTSecret)(jWT: JWT): F[Unit]
}

object JWTService {
  def create[F[_]: Sync]: JWTService[F] = new labs.sec.jwt.impl.JWTServiceTSECImpl()
}
