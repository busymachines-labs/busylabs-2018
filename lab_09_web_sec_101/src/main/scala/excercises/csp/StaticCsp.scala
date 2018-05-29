package excercises.csp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import common.{Config, Endpoint}

class StaticCsp(implicit mat: ActorMaterializer) extends Endpoint with SprayJsonSupport {

  override val resourceIdentifier: String = "csp"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    staticRoute
  }
}
