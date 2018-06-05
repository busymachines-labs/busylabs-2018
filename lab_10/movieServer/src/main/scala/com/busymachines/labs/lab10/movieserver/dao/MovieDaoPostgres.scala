package com.busymachines.labs.lab10.movieserver.dao

import doobie._
import doobie.implicits._
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}

class MovieDaoPostgres (psqlURL: String, psqlUser: String, psqlPassword: String)(implicit ec: ExecutionContext) extends MovieDao {


  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", psqlURL, psqlUser, psqlPassword
  )

  override def getAllMovies(): Future[List[MovieDbObject]] =
    sql"select * from movies".
      query[MovieDbObject].to[List].transact(transactor).unsafeToFuture()

  override def getMovie(id: String): Future[Option[MovieDbObject]] =
    sql"select * from movies where id = ${id}::uuid".
      query[MovieDbObject].option.transact(transactor).unsafeToFuture()

  override def addMovie(title: String, year: Int, director: String, rating: Double): Future[String] =
    { val newId = java.util.UUID.randomUUID().toString
      sql"INSERT INTO movies (id, title, year, director, rating) VALUES (${newId}::uuid, ${title}, ${year}, ${director}, ${rating})".
        update.run.transact(transactor).unsafeToFuture().map(_ => newId)
    }

  override def deleteMovie(id: String): Future[String] =
    sql"DELETE FROM movies WHERE id = ${id}::uuid".
      update.run.transact(transactor).unsafeToFuture().map(_ => id)

  override def updateMovie(id: String, title: String, year: Int, director: String, rating: Double): Future[String] =
    sql"UPDATE movies SET title = ${title}, year = ${year}, director = ${director}, rating = ${rating} WHERE id = ${id}::uuid".
      update.run.transact(transactor).unsafeToFuture().map(_ => id)
}