package me.jeffshaw.inotifywait

import java.nio.file.Files
import cats.effect.IO
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll}
import scala.concurrent.{ExecutionContext, Future}

class InotifyWaitIterantSpec extends AsyncFunSuite with BeforeAndAfterAll {

  override implicit def executionContext: ExecutionContext = ExecutionContext.global

  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF = Future(InotifyWaitIterant[IO](testDir, false, Set()).take(4).toListL.unsafeRunSync())

    Thread.sleep(1000L)

    val expectedEvents = InotifyWaitSpec.createEvents(tempFile)

    for {
      result <- resultF
    } yield {
      assertResult(expectedEvents)(result)
    }
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
