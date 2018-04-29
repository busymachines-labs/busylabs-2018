# On effects and side-effects

The beauty of functional programming in its most modern conception is that it allows us to get behaviour that is earily reminiscent of "language features" built from a few fundamental and basic language building blocks. Thus not requiring any language feature (read: compiler magic) to get things done. This extends from simple things like "absence of value" (e.g. null [1]), going through abnormal behavior (e.g. exceptions) to complicated things like mutation (and most importantly clean-up of the residual side-effects of side-effects (yeah, that's how bad it gets that I need nested parentheses) e.g. closing sockets, file-handles, etc.), up through to concurrency. The most important thing being is that it expresses all these very disparate concepts using common abstractions. Something that does not exist when dealing with "language features" directly.

If we do functional programming properly we can express *all* these things in terms of various "effects" — a term whose formal definition will always elide us, but of which we can gain a pretty good intuition. But it is important to not confuse "effects" with "side-effects", which is a different thing, therefore from this point onward I will refer to code that does side-effects as "impure". Required reading for this part is Alexandru Nedelcu's essay: ["What is functional programming?"](https://alexn.org/blog/2017/10/15/functional-programming.html)

To enable us to reason in terms of "effects" we have to follow, what is essentially, the iron rule of functional programming is: "any impure computation has to be suspended in the appropriate _effect_". This rule is non-negociable, if you're breaking it then you're doing imperative programming, end of story [2].

## the evil of `throw`

Examples lifted from [`lab_02`](/lab_02), don't forget to check that out. Impure code:
```scala
import busymachines.core._

//with this occassion we'll introduce value classes:
//see: https://docs.scala-lang.org/overviews/core/value-classes.html
//they're not that important
final class Password private (val plainText: String) extends AnyVal

object Password {
  //this is an impure method because it can throws an exception and
  //immediately intrerups your program. This is a side-effect
  def apply(pw: String): Password =
    if (pw.length < 6) throw InvalidInputFailure("Password needs to have at least 6 characters")
    else new Password(pw)

}

object ImpurePWCreation {
  //remember the talk about special language features? Well, that shit is does
  //not come without many, many problems of its own.
  val pw = Password("12345")

  println(pw)
}
```

The functional programming variant:

```scala
import busymachines.core._
import busymachines.effects._ //bring in Result

final class Password private (val plainText: String) extends AnyVal

object Password {

  //alternatively you can use scala.util.Try but `Result` has
  //better interop with the larger (non-shit) eco-system.
  //There's also a larger (shit) eco-system lead by Lightbend itself
  def apply(pw: String): Result[Password] =
    if (pw.length < 6) Result.fail(InvalidInputFailure("Password needs to have at least 6 characters"))
    else Result.pure(new Password(pw))
}

object PurePWCreation {
  val pw = Password("12345")
  //nothing happens. at this point in the program. Instead of throwing the exception,
  //we modeled it as data. Therefore, our program is not subjected to bludgeoning
  //by the barbaric "throw".

  //impure, but `Result`/`Try` are not the appropriate effects for this
  println(pw)
  //this prints:
  //Left(busymachines.core.InvalidInputFailureImpl: Password needs to have at least 6 characters)
}
```

N.B.
The way we dealt with the impure `throw` statement was by simply using an alternative way of representing "failure". If you have to interact with code that actually throws things you can:

```scala
import busymachines.effects._
val r = Result(throw new RuntimeException("I'm here to crash your program!"))
//alternatively:

import scala.util.Try
val r2 = Try(throw new RuntimeException("I'm here to crash your pogram again!"))
//program didn't crash.
```

See `lab_02` for an extended example that includes `Email` and `User`, and how to combine them. Also see "Essential Scala" chapter 6.3 for `for comprehensions`.

### N.B. on nomenclature

The process of transforming some sort of evil impure computation into an effect will be refered to as "suspending into ${EffectName}". So in this case we suspend exception throwing into `Result`. We will be using the same expression when talking about suspending `side-effects`, and `null`. Also note that `Try/Result` are only good at suspending the impurity of "throw". And not for anything else.

## the evil of `null`
The code bellow is not impure, per-se. `null` is just a value like any other, not that different from say, `0`.

```scala
//pure code
val f: String = null
```

The problem is that it causes impurities everywhere else in our code by virtue of throwing `NullPointerException` in your face and ruining your day.
```scala
val f: String = null

//impure — even though it shouldn't be
f.toLowercase()
```

As a nit-picky note, because in Scala technically everything can be `null`, all our code is potentially impure. Which is completely unacceptable for any reasonable workflow in any pure functional language. So to make things usable, we have to purge `null` with steadfast determination and will from our code. Which is made easy by using _only_ the effect `Option` (see lab 2) to model "absence of value".

But the good news is that in [Scala 3](https://contributors.scala-lang.org/t/non-nullability-by-default/964/16) non-nullability will be the default behaviour, meaning that we will not be living in constant danger anymore.

## the evil of mutation

This is a rather broad category, and it includes both doing mutation, and reading stuff that is mutated either by us, some other part of the application, or the system:
- global mutable state. Emphasis on global. Using a mutable collection that never escapes the method that declares it is fine, because said method never breaks [referential transparency](https://wiki.haskell.org/Referential_transparency). Technically, you could talk about an entire "module" being referentially transparent, but because no software engineer on earth managed to create a perfectly composable module, it's the wrong level of granularity for pretty much all practical purposes. This is also global mutable state:
   - `java.time.LocalDate.now()` or `java.lang.System.currentTimeMillis()` — even if you only read it, it still counts
   - `scala.util.Random.nextLong()` whenever you are asking for a new number, you are doing some global mutable
- any kind of file system I/O, reading files, reading from the network, binding ports, etc.
- database read/writes

All of the above things are necessary to write useful programs. But they are also extremely dangerous, and frankly, all of the programming you've been taught has been completely, and utterly reckless about these things. If driving was being taught like this everyone would be dead.

Now, the most appropriate effect to use to suspend all these operations is `cats.effect.IO` (available through a `busymachines.effects._` import as well).

```scala
import java.io.File

object ImpureCreation extends App {
  def createFile(fullPath: String): File = {
    //this is ok, nothing impure happening. We're just
    //creating a data structure
    //btw, the Java stdlib can't interpret the `~` as "home",
    //but too lazy to type out entire path
    val f = new File(fullPath)

    //not ok, this is impure, as  the value of this depends on something
    //outside of our program that could be modified completely independently
    if (f.exists()) {
      println("... file does no exist. Creating")
      //double bad: not only can this throw an exception in your face
      //but it also writes to disk!
      f.createNewFile()
    }
    else {
      println("... found file")
    }
    f
  }
}
```

The purified version would look like:
```scala
import java.io.File
import cats.effect.IO

object PureCreation extends App {
  def createFile(fullPath: String): IO[File] = {
    val f = new File(fullPath)

    for {
      exists <- IO(f.exists)
      _ <- if (exists) IO {
            println("... file does not exist. Creating")
            f.createNewFile()
          }
          else IO(println("... found file"))
    } yield f
  }
}
```

## concurrency and mutation

In the Lightbend Scala ecosystem ([akka](https://doc.akka.io/docs/akka-http/current/), [slick](http://slick.lightbend.com/), etc), `scala.concurrent.Future` is used to "suspend" side-effects. Unfortunately, it is woefully inadequate for the job. See Rob Norris' [explanation as to why](https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/cyns21h/). If you just want cheap, easy parallelism that does only pure computation, then `Future` is ok for the job, otherwise try avoiding it like the plague (and mind that the plague spread slower through medieval Europe than `Future` spreads through your code).

To learn about how to use `Future`s check out the [official documentation](https://docs.scala-lang.org/overviews/core/futures.html)

You can use `cats.effect.IO` as an alternative to `Future`, but it can work as such with quite a lot of manual scheduling, but `monix.eval.Task` is a better fit because it was designed and optimized for concurrency and parallelism. All three effects are available through a `busymachines.effects._` import. And `Task` is easily convertable to a future through a `task.runAsync()`, or `task.asFutureUnsafe()`(available only w/ the `busymachines.effects._` import)

## Conclusion

Basically, for most of our applications we will need only 3 effects:
- `Option` — used to model absence of value
- `Result/Try` — used to model "failure"
- `Task` (with the ocasional transforming it to a future if there's legacy code involved) — used to suspend side-effects and handle concurrency

## What is to be done?

Take inspiration from lab_02, and lab_05 and create an interface that can support the following things:
- have a minimal definition of a "User"
- persist said users (in memory is fine), but only if they have 100% valid data (for reasonable definitions of "valid")
- have a minimal definition of a ``"session"` for a given `User`, a way to create it iff the `User` can prove their identity, and identify a user by a `session`
- a `User` should be able to update their details [iff](https://en.wikipedia.org/wiki/If_and_only_if) they can prove to have a valid session for said users
- it has tot be thread-safe
- do it the pure FP way, no compromise
- make sure that the rationale of your design is clearly stated

How to go about it:
- create a [fork](https://help.github.com/articles/fork-a-repo/) of the `busylabs` repository
- create a fully working program on [a branch](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging) in your fork. Put it under the submodule `lab_07`
- make sure to push all your changes on your branch
- open a [pull-request](https://help.github.com/articles/creating-a-pull-request-from-a-fork/) against the main repository
- you don't have to work alone, this is not a contest

After reviewing all solutions we will pick the one (or a hybrid of multiple) to serve as an example for everyone going forward. Good luck! There is no real deadline, just steadfast determination to go forward (what is this, school?).

## Footnotes:
[1] Null, far from being a simple thing is  actually a needless complication in language design. The simple fact that most languages do not model this semantically special "effect" in terms of existing data-definition features, and add a special keyword for it ought to be enough of starting point for you to think further about the implications. `Null` has not been deemed the ["billion dollar mistake"](https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare) by its creator for nothing.
[2] This extends up to trivial things like a `println` — for debugging purposes, and if they're used in a transitory manner they're ok to use without any suspension. But if what is printed out is important for the _correct functioning of your program_ (i.e. it's part fo the requirements) then they absolutely need to be suspended in the appropriate effect.

## Collected references (in no particular order):
- `for comprehensions` — see Essential Scala chapter 6.3 onwards
- [value classes](https://docs.scala-lang.org/overviews/core/value-classes.html), i.e. `class X extends AnyVal`
- ["What is Functional Programming?"](https://alexn.org/blog/2017/10/15/functional-programming.html)
- [non-nulability](https://contributors.scala-lang.org/t/non-nullability-by-default/964/16)
- [the unusability of Future](https://www.reddit.com/r/scala/comments/3zofjl/why_is_future_totally_unusable/cyns21h/)
- [Future](https://docs.scala-lang.org/overviews/core/futures.html)
- [fork on github](https://help.github.com/articles/fork-a-repo/)
- [create pull request](https://help.github.com/articles/creating-a-pull-request-from-a-fork/)
- [git branch](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging)
