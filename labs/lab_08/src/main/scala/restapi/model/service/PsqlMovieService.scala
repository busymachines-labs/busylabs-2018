package restapi.model.service

import restapi.model.dao.{Movie, MovieNotFoundException, MovieWithoutId}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Random, Try}

/*
* Disclaimer: This class is badly implemented not using the moset efficient slick constructs to acess the data.
* DO NOT TAKE IT AS A WAY-Of-Doing-Things when working with psql in scala. It's just a proof of concept for other things we are working on.
* */
class PsqlMovieService(initialMovie: MovieWithoutId, dbHost: String, dbUser: String, dbPassword: String, dbName: String)(implicit ec: ExecutionContext) extends MovieService {
  val connectionUrl = s"jdbc:postgresql://${dbHost}/${dbName}?user=${dbUser}&password=${dbPassword}"
  val profile = slick.jdbc.PostgresProfile
  import profile.api._
  val db = Database.forURL(connectionUrl, driver = "org.postgresql.Driver")
  db.createSession()

  // Create the Table if it doesn't exist yet. The statement will fail if the db exists so we wrap it in a Try
  Try{Await.result(db.run(sqlu"CREATE TABLE movies (id varchar(20), title varchar(20), year integer, rating decimal);"), 5 minutes)}


  class MoviesSchema(tag: Tag) extends Table[Movie](tag, "movies") {
    def id = column[String]("id", O.PrimaryKey)
    def title = column[String]("title")
    def year = column[Int]("year")
    def rating = column[Double]("rating")
    def * = (id, title, year, rating) <> ((Movie.apply _).tupled, Movie.unapply)
  }
  val movies = TableQuery[MoviesSchema]

  // Insert Original movie if it doesn't exist
  Await.result(
    for {
      allMovies <- getAllMovies
      _ <- if(allMovies.exists(m => m.rating == initialMovie.rating && m.title == initialMovie.title && m.year == initialMovie.year)) Future.successful("success")
      else addMovie(initialMovie)
    } yield ()
  , 5 minutes)


  // ============================== Actual methods of interest below ==============================================
  override def getAllMovies: Future[Seq[Movie]] = {
    println("Executing getAllMovies() on psql")
    db.run(movies.result)
  }

  override def getMovie(movieId: String): Future[Movie] = {
    println(s"Executing getMovie(${movieId}) on psql")
    for {
      data <- db.run(movies.filter(_.id === movieId).result)
    } yield data.headOption.getOrElse(throw MovieNotFoundException(movieId))
  }

  override def addMovie(movie: MovieWithoutId): Future[String] = {
    println(s"Executing addMovie(${movie}) on psql")
    for {
      newId <- getFreeId
      _ <- db.run(movies.insertOrUpdate(Movie(id = newId, title = movie.title, year = movie.year, rating = movie.rating)))
    } yield newId
  }

  override def deleteMovie(id: String): Future[String] = {
    println(s"Executing deleteMovie(${id}) on psql")
    db.run(movies.filter(_.id === id).delete).map(_ => id)
  }

  override def updateMovie(id: String, movie: MovieWithoutId): Future[String] = {
    println(s"Executing updateMovie(${id}, ${movie}) on psql")
    db.run(movies.insertOrUpdate(Movie(id = id, title = movie.title, year = movie.year, rating = movie.rating))).map(_ => id)
  }

  private def getFreeId: Future[String] = {
    val newId: String = Random.nextInt(100000).toString
    for {
      allMovies <- getAllMovies
      finalId <- if(allMovies.exists(_.id == newId)) getFreeId else Future.successful(newId)
    } yield finalId
  }
}
