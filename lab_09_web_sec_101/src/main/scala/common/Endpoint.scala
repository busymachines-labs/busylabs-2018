package common

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

trait Endpoint {
  val resourcePath, resourceIdentifier: String

  lazy val staticRoute: Route = {
    getFromResource(s"$resourcePath/index.html")
  }

  def routes: Route
}
