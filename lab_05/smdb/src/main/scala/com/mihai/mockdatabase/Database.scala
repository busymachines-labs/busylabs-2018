package com.mihai.mockdatabase

import com.mihai.datamodel.User

import scala.collection.mutable
import scala.util.Random

object Database {
  val db:mutable.HashMap[String, User] = mutable.HashMap[String, User](
    "mihaiId" -> User(name = "mihai", password = "whatever", email = "mihai.simu@busymachines.com", age = 28),
    "danId" -> User(name = "dan", password = "whatever2", email = "dan.vidican@busymachines.com", age = 23),
    "andraId" -> User(name = "andra", password = "whatever3", email = "andra.ille@busymachines.com", age = 30)
  )

  def getAllUsers(): Seq[User] = db.values.toSeq

  def addUser(usr: User): String = {
    val newId: String = Random.nextString(7)
    db.put(newId, usr)
    newId
  }

  def deleteUser(id: String): String = {
    db.remove(id)
    id
  }
}
