package test

import busymachines.core._
import busymachines.effects._

object Password {

  def apply(pw: String): Result[Password] =
    if (pw.length < 6)
      Result.fail(InvalidInputFailure("Password needs to have at least 6 characters"))
    else Result.pure(new Password(pw))

}

final class Password private (val plainText: String) {

  override def equals(other: Any): Boolean = other match {
    case that: Password =>
      plainText == that.plainText
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(plainText)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Email {

  def apply(em: String): Option[Email] =
    if (!em.contains("@")) None else Some(new Email(em))

}

final class Email private (val plainTextEmail: String) {

  override def equals(other: Any): Boolean = other match {
    case that: Email =>
      plainTextEmail == that.plainTextEmail
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(plainTextEmail)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object UserRight {

  def fromName(s: String): Result[UserRight] = {
    s match {
      case "Admin"          => Result.pure(Admin)
      case "Regular"        => Result.pure(Regular)
      case "IAmBecomeDeath" => Result.pure(IAmBecomeDeath)
      case _                => Result.fail(InvalidInputFailure(s"UserRight has to be one of [], but was: $s"))
    }
  }
}

sealed trait UserRight

case object Admin extends UserRight
case object Regular extends UserRight
case object IAmBecomeDeath extends UserRight

case class User(email: Email, pwd: Password, right: UserRight)
