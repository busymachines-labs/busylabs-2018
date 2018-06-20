package labs.email

import labs.email.impl.EmailServiceJavaGmailImpl
import cats.effect.Async

/**
  *
  * This style of writing algebras (in layman terms: interface) is called
  * "final tagless".
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
trait EmailService[F[_]] {

  def sendEmail(to: Email, subject: Subject, content: Content): F[Unit]

}

object EmailService {

  def gmailClient[F[_]: Async](config: GmailConfig): EmailService[F] =
    new EmailServiceJavaGmailImpl(config)
}
