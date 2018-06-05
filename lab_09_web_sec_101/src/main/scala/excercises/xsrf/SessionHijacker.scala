package excercises.xsrf

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{getFromResource, pathPrefix}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RespondWithDirectives
import common.{Config, Endpoint}

class SessionHijacker extends Endpoint with RespondWithDirectives with SprayJsonSupport {
  override val resourceIdentifier: String = "xsrf"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  // Use header to deny certain resources to be loaded
  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    getFromResource(s"$resourcePath/hijack.html")
  }
}
