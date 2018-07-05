package pms.algebra.imdb

import java.util.concurrent.ConcurrentLinkedQueue

import monix.execution.Scheduler.Implicits.global

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}

case class RateLimiter[T](duration: FiniteDuration, maxInvocations: Int) {

  private val queue = new ConcurrentLinkedQueue[() => Promise[T]]()

  global.scheduleAtFixedRate(0.millis, duration) {

    while (!queue.isEmpty) {
      Option(queue.poll()).foreach { fun =>
        fun.apply()
      }
    }
  }

  def apply(f: => Future[T]): Future[T] = {
    val res = Promise[T]()
    queue.add(() => { res.completeWith(f) })
    res.future
  }
}
