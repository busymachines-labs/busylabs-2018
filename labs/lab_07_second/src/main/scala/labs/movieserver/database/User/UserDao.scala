package labs.movieserver.database.User

import labs.movieserver.datamodel.User

import scala.concurrent.Future

trait UserDao {

  def createUser(user: User): Future[String]

  def getUser(id: String): Future[User]

  def updateUser(id: String, user: User): Future[String]

  def deleteUser(id: String): Future[String]
}
