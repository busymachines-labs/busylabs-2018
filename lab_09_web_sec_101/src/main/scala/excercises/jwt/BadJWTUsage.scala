package excercises.jwt

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MarshallingDirectives
import akka.http.scaladsl.server.Route
import common.{Config, Endpoint}
import pdi.jwt.{Jwt,   JwtAlgorithm}

import scala.util.{Failure, Success, Try}

/**
  * Safe JWT      - eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IklvbiIsInJvbGUiOiJtZW1iZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.kB1GuviR0sb-RqsfNwv8p0Og468zuYzWZT-tTFW_oUs
  * Attack vector - eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IklvbiIsInJvbGUiOiJhZG1pbiIsImlhdCI6MTUxNjIzOTAyMn0.
  */

class BadJWTUsage extends Endpoint with MarshallingDirectives with SprayJsonSupport {
  import spray.json._
  import DefaultJsonProtocol._

  override val resourceIdentifier: String = "jwt"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  implicit val jwtHeaderJson:  RootJsonFormat[JwtHeader]  = jsonFormat2(JwtHeader)
  implicit val jwtPayloadJson: RootJsonFormat[JwtPayload] = jsonFormat4(JwtPayload)

  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    path("sensitiveData") {
      headerValueByName("authToken") { token =>
        get {
          // Bad implementation in older libraries
          val decoded = Jwt.decodeAll(token) match {
            case Failure(p) =>
              Jwt
                .decodeAll(token, "secret", Seq(JwtAlgorithm.HS256))
                .map(d => (d._1.parseJson.convertTo[JwtHeader], d._2.parseJson.convertTo[JwtPayload]))
            case Success(d) =>
              Try((d._1.parseJson.convertTo[JwtHeader], d._2.parseJson.convertTo[JwtPayload]))
          }

          println(decoded)

          // Result of jwt interpretation
          decoded match {
            case Success((_, JwtPayload(_, _, "member", _))) => complete(200 -> "Hello Member")
            case Success((_, JwtPayload(_, _, "admin",  _))) => complete(200 -> "Hello Admin (Access to sensitive data)")
            case _ => complete(401 -> "Shoo")
          }
        }
      }
    }
  }
}
