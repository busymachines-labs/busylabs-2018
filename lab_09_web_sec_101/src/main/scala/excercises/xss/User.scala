package excercises.xss

import akka.actor.{Actor, ActorRef}

object User {
  case class Connected(outgoing:   ActorRef)
  case class IncomingMessage(text: String)
  case class OutgoingMessage(text: String)
}

class User(chatRoom: ActorRef) extends Actor {
  import User._

  def receive = {
    case Connected(outgoing: ActorRef) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    chatRoom ! ChatRoom.Join

    {
      case IncomingMessage(text) =>
        chatRoom ! ChatRoom.ChatMessage(text)

      case ChatRoom.ChatMessage(text) =>
        outgoing ! OutgoingMessage(text)
    }
  }
}
