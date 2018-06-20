import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import excercises.SecurityExercisesAssembly

object Main extends App with SecurityExercisesAssembly {
  val conf          = ConfigFactory.load
  val host          = conf.getString("app.host")
  val mainPort      = conf.getInt("app.safePort")
  val maliciousPort = conf.getInt("app.maliciousPort")

  val main      = Http().bindAndHandle(MainRoutes,      host, mainPort)
  val malicious = Http().bindAndHandle(MaliciousRoutes, host, maliciousPort)
}
