package test

import busymachines.core._
import busymachines.effects._
//see http://busymachines.github.io/busymachines-commons/docs/effects.html#validated
//TODO: accumulate the errors instead of having fail first semantics

object UserService {
  
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
      pw <- Password(plainTextPw)
      em <- Email(email)
      ur <- UserRole.fromName(right)
      _ <- if (doesEmailExist(em))
            Result.fail(ConflictFailure(s"Email: $em is already registered to a different user"))
          else Result.unit
      user = User(email = em, pwd = pw, ur)
      _ <- Result(createUser(user))
      _ <- Result(sendEmailToUser(user))
    } yield user
    //    the desugared version of the above is roughly:
    //    Password(plainTextPw).flatMap { pw: Password =>
    //      Email(email).flatMap { em =>
    //        UserRight.fromName(right).flatMap { ur =>
    //          val eff =
    //            if (doesEmailExist(em))
    //              Result.fail(ConflictFailure(s"Email: $em is already registered to a different user"))
    //            else Result.unit // == Some(())
    //          val user = User(em, pw, ur)
    //          createUser.flatMap {_ =>
    //            sendEmailToUser(user)
    //              .map(_ => user)
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
