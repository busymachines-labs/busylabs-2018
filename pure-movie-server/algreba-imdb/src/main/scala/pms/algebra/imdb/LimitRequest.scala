package pms.algebra.imdb

import java.util.concurrent.ConcurrentLinkedQueue

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit.MILLISECONDS
class LimitRequest[T](duration: FiniteDuration, maxRequests: Int){
  import monix.execution.Scheduler.Implicits.global

  private val queue = new ConcurrentLinkedQueue[() => Promise[T]]()

  global.scheduleAtFixedRate(FiniteDuration(0,MILLISECONDS), duration) {
    while (!queue.isEmpty) {
      Option(queue.poll()).foreach { fun =>
        fun.apply()
      }
    }

  }

  def apply(f: => Future[T]): Future[T] ={
    val res = Promise[T]()
    queue.add(() => { res.completeWith(f) })
    res.future
  }

}
/*val mvar = MVar.apply(Option[Movie])
 import monix.execution.Scheduler.Implicits.global

 global.scheduleAtFixedRate(duration, duration) {
   this synchronized {
     Option(mvar.runSyncUnsafe(duration).take.foreach { fun =>
         fun.apply(Scraper.scrapeByTitle())
       })
     }
   }
 }

 def apply[T](f: => Future[T]): Future[T] =
   this synchronized {
     if (permits > 0) {

       f
     } else {
       val res = Promise[T]()
       queue.add(() => { res.completeWith(f) })
       res.future
     }
     */