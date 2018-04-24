package com.mihai.api

import busymachines.json._
import busymachines.rest.jsonrest._

import akka.http.scaladsl.server.Directives.{complete, get, path, post}
import com.mihai.datamodel.User
import com.mihai.mockdatabase.Database

import akka.http.scaladsl.server.Directives._

trait UserApi extends JsonSupport {

  implicit val userSerializer: Codec[User] = derive.codec[User]

  val userRoute =
    path("user") {
      get {
        complete(Database.getAllUsers())
      } ~
        post {
          entity(as[User]) { usr =>
            complete(Database.addUser(usr))
          }
        }
    } ~ path("user" / Segment) { userId =>
      delete(
        complete(Database.deleteUser(userId))
      )
    }

}
