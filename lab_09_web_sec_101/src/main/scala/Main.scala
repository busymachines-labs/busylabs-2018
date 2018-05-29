import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import excercises.SecurityExercisesAssembly

object Main extends App with SecurityExercisesAssembly {
  val conf = ConfigFactory.load
  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")

  val bindingFuture = Http().bindAndHandle(AppRoutes, host, port)
}
