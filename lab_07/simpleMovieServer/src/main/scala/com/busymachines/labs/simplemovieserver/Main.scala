package com.busymachines.labs.simplemovieserver

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object Main extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher



  val conf = ConfigFactory.load

  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")

  val initialUserEmail = conf.getString("initialuser.email")
  val initialUserAge = conf.getInt("initialuser.age")

  println(s"Starting Server at ${host}:${port}")
  println(s"Adding initial user [email = ${initialUserEmail}; age = ${initialUserAge}]")
}
