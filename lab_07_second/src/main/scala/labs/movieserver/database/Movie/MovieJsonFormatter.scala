package labs.movieserver.database.Movie

import labs.movieserver.datamodel.Movie
import spray.json._

trait  MovieJsonFormatter {
  import spray.json.DefaultJsonProtocol._

  implicit val formatter: RootJsonFormat[Movie] = jsonFormat4(Movie)
}
