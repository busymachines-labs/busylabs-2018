package com.busymachines.labs.lab10.movieserver.dao

import doobie._
import doobie.implicits._
import busymachines.effects._

class MovieDaoPostgres (psqlURL: String, psqlUser: String, psqlPassword: String) extends MovieDao {


  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", psqlURL, psqlUser, psqlPassword
  )

  //def getAllMoviesWithComments: IO[List[(MovieDbObject, Option[MovieCommentDbObject])]] =
  //  sql"select * from movies m left outer join moviecomments c on m.id = c.movie_id".
  //    query[(MovieDbObject, Option[MovieCommentDbObject])].to[List].transact(transactor).unsafeToIO()

  override def getAllMovies(): IO[List[MovieDbObject]] =
    sql"select * from movies".
      query[MovieDbObject].to[List].transact(transactor)

  override def getMovie(id: String): IO[Option[MovieDbObject]] =
    sql"select * from movies where id = ${id}::uuid".
      query[MovieDbObject].option.transact(transactor)

  override def addMovie(title: String, year: Int, director: String, rating: Double): IO[String] =
    { val newId = java.util.UUID.randomUUID().toString
      sql"INSERT INTO movies (id, title, year, director, rating) VALUES (${newId}::uuid, ${title}, ${year}, ${director}, ${rating})".
        update.run.transact(transactor).map(_ => newId)
    }

  override def deleteMovie(id: String): IO[String] =
    sql"DELETE FROM movies WHERE id = ${id}::uuid".
      update.run.transact(transactor).map(_ => id)

  override def updateMovie(id: String, title: String, year: Int, director: String, rating: Double): IO[String] =
    sql"UPDATE movies SET title = ${title}, year = ${year}, director = ${director}, rating = ${rating} WHERE id = ${id}::uuid".
      update.run.transact(transactor).map(_ => id)
}