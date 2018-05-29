import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object Main extends App {
  implicit val system           = ActorSystem("my-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val conf = ConfigFactory.load

  println("Running...")
}
