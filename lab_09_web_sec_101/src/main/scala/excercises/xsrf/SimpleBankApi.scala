package excercises.xsrf

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.directives.CookieDirectives
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import common.{Config, Endpoint}

class SimpleBankApi(implicit mat: Materializer) extends Endpoint with CookieDirectives with SprayJsonSupport {
  import spray.json._
  import DefaultJsonProtocol._

  override val resourceIdentifier: String = "xsrf"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  var usersBankAcc  = BankAccount("Bob", 2000)
  val userAuthToken = "safeAuthToken-" + Math.random().toString

  implicit val bankAccFormatter:     RootJsonFormat[BankAccount]     = jsonFormat2(BankAccount)
  implicit val transferReqFormatter: RootJsonFormat[TransferRequest] = jsonFormat2(TransferRequest)

  val csrfToken = "ngboqwjbe21veszcopvjbjwen312"

  // Use header to deny certain resources to be loaded
  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    path("login") {
      get {
        // Authentication step
        setCookie(HttpCookie("authToken", userAuthToken, httpOnly = false)) {
          complete(200 -> "Logged in")
        }
      }
    } ~ get {
      path("account") {
        complete(200 -> usersBankAcc)
      } ~ path("transfer") {
        // Should verify session cookie first
        parameters("to", "amount") { (to, amount) =>
          usersBankAcc = usersBankAcc.transfer(to, amount.toInt)

          val successMessage = s"Transferred $amount to $to"
          complete(200 -> successMessage)
        }
      }
    } ~ staticRoute
  }
}
