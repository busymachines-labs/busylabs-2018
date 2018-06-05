package com.busymachines.labs.lab10.movieserver.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, path, post}
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import com.busymachines.labs.lab10.movieserver.service.MovieService

import scala.concurrent.ExecutionContext

class MovieApi(movieService: MovieService)(implicit ec: ExecutionContext) extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val movieSerializer: RootJsonFormat[MovieDto] = jsonFormat5(MovieDto)
  implicit val movieCreateSerializer: RootJsonFormat[MovieCreate] = jsonFormat4(MovieCreate)


  val movieRoute =
    pathPrefix("v1") {
      handleExceptions(globalExceptionHandler) {
        pathPrefix("movie") {
          pathEndOrSingleSlash{
            get {
              complete(movieService.getAllMovies.unsafeToFuture())
            } ~
              post { entity(as[MovieCreate]) { newMovie =>
                complete(movieService.addMovie(newMovie).map(_.toString).unsafeToFuture())
              }}
          } ~
            path(Segment) { movieId: String =>
              get {
                complete(movieService.getMovie(movieId).unsafeToFuture())
              } ~
                put {entity(as[MovieCreate]) { newMovie =>
                  complete(movieService.updateMovie(movieId, newMovie).map(_.toString).unsafeToFuture())
                }} ~
                delete {
                  complete(movieService.deleteMovie(movieId).map(_.toString).unsafeToFuture())
                }
            }
        }
      }
    }


  val globalExceptionHandler = ExceptionHandler {
    //case ex: //MovieNotFoundException => {
    //  println(s"Not Found Movie ${ex.movieId}")
    //  complete(HttpResponse(StatusCodes.NotFound, entity = ex.getMessage()))
    //}
    case ex: Exception => {
      println(s"General Exception : ${ex.getMessage}")
      complete(HttpResponse(StatusCodes.InternalServerError, entity = ex.getMessage()))
    }
  }


}
