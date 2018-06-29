package labs.sec.jwt.impl

import busymachines.core.InvalidInputFailure
import cats.effect.Sync
import tsec.jwt._
import tsec.jws.mac._
import tsec.mac.jca._
import tsec.common._
import busymachines.duration
import labs.sec.jwt._

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
private[jwt] class JWTServiceTSECImpl[F[_]: Sync] extends JWTService[F] {
  import cats.implicits._
  private val F = Sync[F]

  override def issue(secret: JWTSecret): F[JWT] =
    for {
      key    <- HMACSHA256.buildKey[F](secret.getBytes)
      claims <- JWTClaims.withDuration[F](expiration = Some(duration.minutes(20)))
      jwt    <- JWTMac.build[F, HMACSHA256](claims, key) //You can sign and build a jwt object directly
    } yield jwt.toEncodedString

  def verify(secret: JWTSecret)(plainJWT: JWT): F[Unit] =
    for {
      key    <- HMACSHA256.buildKey[F](secret.getBytes)
      status <- JWTMac.verifyFromString[F, HMACSHA256](plainJWT, key)
      _ <- status match {
            case Verified           => F.unit
            case VerificationFailed => F.raiseError(InvalidInputFailure(s"Invalid JWT: ${plainJWT}"))
          }
    } yield ()

}
