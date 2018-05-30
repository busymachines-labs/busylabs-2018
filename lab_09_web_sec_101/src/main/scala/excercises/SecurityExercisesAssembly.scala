package excercises

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import common.{Config, Endpoint}
import excercises.csp.StaticCsp
import excercises.jwt.BadJWTUsage
import excercises.xsrf.{SessionHijacker, SimpleBankApi}
import excercises.xss.EchoChat

trait SecurityExercisesAssembly extends SprayJsonSupport {

  implicit val system           = ActorSystem("my-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val indexName = Config.resourcePath("index.html")

  lazy val indexRoute: Route = path("") {
    getFromResource(indexName)
  }

  val endpoints: List[Endpoint] = List(
    new EchoChat,
    new StaticCsp,
    new BadJWTUsage,
    new SimpleBankApi
  )

  private val notFound = complete(404 -> "Not found")

  def MainRoutes: Route =
    endpoints.foldLeft(indexRoute)((acc, current) => acc ~ current.routes) ~ notFound

  def MaliciousRoutes: Route = {
    val hijacker = new SessionHijacker

    hijacker.routes ~ notFound
  }

}
