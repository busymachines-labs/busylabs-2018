package test

import busymachines.core._
import busymachines.effects._
//see http://busymachines.github.io/busymachines-commons/docs/effects.html#validated
//TODO: accumulate the errors instead of having fail first semantics

object UserService {

  //this is just stuff about covariance(+) and contravariance(-)
//  class Box[-T, +R] {
//    def v(t: T): R = ???
//  }
//
//  class UserRightsBox extends Box[UserRight, IAmBecomeDeath.type] {
//    override def v(t: UserRight): IAmBecomeDeath.type = ???
//  }
//
//  val box1 = new UserRightsBox //new Box[Regular.type, UserRight]
//  //100 lines of code
//  val value = box1.v(Regular)
  //value

//  val box2 = new Box[UserRight, IAmBecomeDeath.type]
//  box2.v(Regular)

//  val boxRegular: Box[Regular.type] = Box[Regular.type](Regular)

//  val x: Box[Regular.type] = Box[UserRight](IAmBecomeDeath)

//  val r: Result[UserRight] = Result[Regular.type](Regular)

  //writes to Database
  def createUser(u: User): Unit = println(
    s"created User: $u"
  )

  def sendEmailToUser(u: User): Unit = println(
    s"sent registration email to user: $u"
  )

  def doesEmailExist(e: Email): Boolean = {
    fetchEmailFromBD(e).nonEmpty //fake going to the DB
  }

  def fetchEmailFromBD(e: Email): Option[Email] = {
    if (e.plainTextEmail == "1@2.com") Option(e) else None
  }

  def registerNewUser(
    email:       String,
    plainTextPw: String,
    right:       String
  ): Result[User] = {
    for {
      pw <- (Password(plainTextPw): Result[Password])
      em <- Email(email).asResult(InvalidInputFailure(s"Email was invalid: $email"))
      ur <- UserRight.fromName(right)
      _ <- if (doesEmailExist(em))
            Result.fail(ConflictFailure(s"Email: $em is already registered to a different user"))
          else Result.unit // == Some(())
      user = User(email = em, pwd = pw, ur)
      _ <- Result(createUser(user))
      _ <- Result(sendEmailToUser(user))
    } yield user
//    Password(plainTextPw).flatMap { pw: Password =>
//      Email(email).asResult(InvalidInputFailure(s"Email was invalid: $email")).flatMap { em =>
//        UserRight.fromName(right).flatMap { ur =>
//          val eff =
//            if (doesEmailExist(em))
//              Result.fail(ConflictFailure(s"Email: $em is already registered to a different user"))
//            else Result.unit // == Some(())
//          eff.map { _ =>
//            val user = User(em, pw, ur)
//            createUser(user)
//            sendEmailToUser(user)
//            user
//          }
//        }
//      }
//    }
  }
}

//================================================================================

object Main extends App {

  println {
    UserService.registerNewUser("IAmNew@2.com", "123456", "IAmBecomeDeath")
  }

  println("----")

  println {
    UserService.registerNewUser(".com", "123456", "IAmBecomeDeath")
  }

  println("----")

  println {
    UserService.registerNewUser("1@2.com", "1234", "IAmBecomeDeath")
  }

}
