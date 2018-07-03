package pms.algebra.imdb.extra

import java.util.concurrent.ConcurrentLinkedQueue

import pms.effects.Future

import scala.concurrent.Promise
import scala.concurrent.duration._
import monix.execution.Scheduler.Implicits.global

case class RateLimiter[T](interval : FiniteDuration, size : Int) {

  private val requestQueue = new ConcurrentLinkedQueue[() => Promise[T]]()

  global.scheduleAtFixedRate(0.millis, interval) {
    for(_ <- 0 until size if !requestQueue.isEmpty) {
      val request = requestQueue.poll()
      request.apply()
    }
  }

  def addToQueue(f : => Future[T]): Future[T] = {
    val promise = Promise[T]()
    requestQueue.add(() => { promise.completeWith(f) })
    promise.future
  }
}
