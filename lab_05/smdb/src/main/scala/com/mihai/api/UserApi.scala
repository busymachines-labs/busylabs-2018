package com.mihai.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path, post}
import com.mihai.datamodel.User
import com.mihai.mockdatabase.Database
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import akka.http.scaladsl.server.Directives._
trait UserApi extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userSerializer: RootJsonFormat[User] = jsonFormat4(User)

  val userRoute =
    path("user") {
      get {
        complete(Database.getAllUsers())
      } ~
      post { entity(as[User]) { usr =>
        complete(Database.addUser(usr))
      }}
    } ~ path("user" / Segment) {userId =>
      delete(
        complete(Database.deleteUser(userId))
      )
    }

}
