package excercises.xss

import akka.NotUsed
import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, OverflowStrategy}
import common._

import scala.collection.mutable

class EchoChat(implicit system: ActorSystem, mat: ActorMaterializer) extends Endpoint with SprayJsonSupport {
  import spray.json.DefaultJsonProtocol._

  override val resourceIdentifier: String = "xss"
  override val resourcePath:       String = Config.resourcePath(resourceIdentifier)

  var messages: mutable.MutableList[String] = mutable.MutableList.empty[String]

  private val chatRoom = system.actorOf(Props(new ChatRoom), "chat")

  def userConnection: Flow[Message, Message, NotUsed] = {
    // new connection - new user actor
    val userActor = system.actorOf(Props(new User(chatRoom)))

    val incomingMessages: Sink[Message, NotUsed] =
      Flow[Message].map {
        // transform websocket message to domain message
        case TextMessage.Strict(text) => {
          messages = messages :+ text
          User.IncomingMessage(text)
        }
      }.to(Sink.actorRef[User.IncomingMessage](userActor, PoisonPill))

    val outgoingMessages: Source[Message, NotUsed] =
      Source
        .actorRef[User.OutgoingMessage](10, OverflowStrategy.fail)
        .mapMaterializedValue { outActor =>
          // give the user actor a way to send messages out
          userActor ! User.Connected(outActor)
          NotUsed
        }
        .map(
          // transform domain message to web socket message
          (outMsg: User.OutgoingMessage) => TextMessage(outMsg.text)
        )

    // then combine both to a flow
    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
  }

  lazy val routes: Route = pathPrefix(resourceIdentifier) {
    path("socket") {
      handleWebSocketMessages(userConnection)
    } ~ get {
      path("messages") {
        complete(200 -> messages.toList)
      } ~ staticRoute
    }
  }
}
