package me.jeffshaw.inotifywait

import java.nio.file.Files
import cats.effect.IO
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}

class InotifyWaitIterantSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val p = Promise[List[Events]]()

    Future(
      InotifyWaitIterant[IO](testDir, false, Set()).take(1).toListL.unsafeRunAsync {
        case Left(failure) => p.failure(failure)
        case Right(result) => p.success(result)
      }
    )

    val expectedEvents = InotifyWaitSpec.createEvents(tempFile)

    val result = Await.result(p.future, Duration.Inf)

    assertResult(expectedEvents)(result)
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
