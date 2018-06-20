package labs.sec.jwt

import busymachines.effects._
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.scalatest.prop.PropertyChecks

/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 05 Jun 2018
  *
  */
class JWTTest extends FunSpec with PropertyChecks with Matchers {
  private def test: ItWord = it

  describe("JWT") {
    test("JWT") {
      val jwtService = JWTService.create[IO]

      forAll { secret: String =>
        whenever(secret.nonEmpty) {

          val io = for {
            j <- jwtService.issue(secret)
            _ <- jwtService.verify(secret)(j)
            _ <- IO(println(j))
          } yield ()

          noException shouldBe thrownBy {
            io.unsafeRunSync()
          }
        }
      }
    }
  }
}
