package labs.movieserver.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import labs.movieserver.database.MovieQuote.MoviQuoteDao
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, get, path, post}
import spray.json.{DefaultJsonProtocol, JsNumber, JsValue, RootJsonFormat}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import labs.movieserver.datamodel.{MovieNotFoundException, MovieQuote, MovieQuoteWithoutId}

class MovieQuoteApi(quoteDAO: MoviQuoteDao) extends SprayJsonSupport  {

  import DefaultJsonProtocol._


    implicit val movieFormat: RootJsonFormat[MovieQuoteWithoutId] = jsonFormat2(MovieQuoteWithoutId)
    implicit val movieIdFormat  = jsonFormat3(MovieQuote)

    val movieQuoteRoute =
      pathPrefix("v1") {
        handleExceptions(globalExceptionHandler) {
          pathPrefix("quotemovie") {
            pathEndOrSingleSlash {
                post {
                    entity(as[MovieQuoteWithoutId]) { newQuote =>
                      complete(quoteDAO.addMovieQuote(newQuote))
                    }
                }
            } ~
              path(Segment) { quote =>
                  get {
                    complete(quoteDAO.getMovieQuote(quote))
                  } ~
                  put {entity(as[MovieQuoteWithoutId]) { newQuote =>
                    complete(quoteDAO.updateMovieQuote(???, newQuote))
                  }} ~
                  delete {
                    complete(quoteDAO.deleteMovieQuote(quote))
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
