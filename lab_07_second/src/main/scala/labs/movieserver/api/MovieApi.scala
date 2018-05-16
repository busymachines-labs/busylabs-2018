package labs.simplemovieserver.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, path, post}
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import labs.movieserver.database.Movie.MovieDao
import labs.movieserver.datamodel.{Movie, MovieNotFoundException, MovieWithoutId}
import labs.movieserver.migrationService.MovieService

class MovieApi(movieDAO: MovieDao, movieService: MovieService) extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val movieSerializer: RootJsonFormat[Movie] = jsonFormat4(Movie)
  implicit val movieWithoutIdSerializer: RootJsonFormat[MovieWithoutId] = jsonFormat3(MovieWithoutId)


  val movieRoute =
    pathPrefix("v1") {
      handleExceptions(globalExceptionHandler) {
        pathPrefix("movie") {
          pathEndOrSingleSlash{
            get {
              complete(movieDAO.getAllMovies)
            } ~
            post { entity(as[MovieWithoutId]) { newMovie =>
                complete(movieDAO.addMovie(newMovie))
            }}
          } ~
            path(Segment) { movieId =>
              get {
                complete(movieDAO.getMovie(movieId))
              } ~
                put {entity(as[MovieWithoutId]) { newMovie =>
                  complete(movieDAO.updateMovie(movieId, newMovie))
                }} ~
                delete {
                  complete(movieDAO.deleteMovie(movieId))
                }
            } ~ pathPrefix("migration") {
                path(Segment) {
                  rating => get {
                    complete(movieService.filterMovies(rating.toDouble))
                  }
                }
          }
        }
      }
    }


  val globalExceptionHandler = ExceptionHandler {
    case ex: MovieNotFoundException => {
      println(s"Not Found Movie ${ex.movieId}")
      complete(HttpResponse(StatusCodes.NotFound, entity = ex.getMessage()))
    }
    case ex: Exception => {
      println(s"General Exception : ${ex.getMessage}")
      complete(HttpResponse(StatusCodes.InternalServerError, entity = ex.getMessage()))
    }
  }


}
