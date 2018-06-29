package common

object Config {
  val staticPath = "static"

  def resourcePath(dir: String, name: String) = s"$staticPath/$dir/$name"

  def resourcePath(name: String) = s"$staticPath/$name"
}
