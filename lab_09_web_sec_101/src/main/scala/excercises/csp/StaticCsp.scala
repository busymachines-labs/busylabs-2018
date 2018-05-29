package excercises.csp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.directives.RespondWithDirectives
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import common.{Config, Endpoint}

class StaticCsp(implicit mat: ActorMaterializer) extends Endpoint with RespondWithDirectives with SprayJsonSupport {

  override val resourceIdentifier: String = "csp"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  val cspHeaderValue =
    "default-src 'self'; script-src 'self'; object-src 'self'; style-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self'; media-src 'none'; connect-src 'self'; base-uri 'self'; form-action 'self';"
  val cspHeader = RawHeader("Content-Security-Policy", cspHeaderValue)

  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    staticRoute
  }
}
