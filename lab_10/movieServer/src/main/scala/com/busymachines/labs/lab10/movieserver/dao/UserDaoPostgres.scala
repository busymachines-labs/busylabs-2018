package com.busymachines.labs.lab10.movieserver.dao

import doobie._
import doobie.implicits._
import cats.effect.IO

import scala.concurrent.Future

class UserDaoPostgres(psqlURL: String, psqlUser: String, psqlPassword: String) {


  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", psqlURL, psqlUser, psqlPassword
  )

  def getAllUsers: Future[List[UserDbObject]] =
    sql"select * from users".query[UserDbObject].to[List].transact(transactor).unsafeToFuture()

}

case class UserDbObject(id: Int, firstName: String, lastName: String, email: String)