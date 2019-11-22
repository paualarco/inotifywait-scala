package me.jeffshaw.inotifywait

import java.nio.file.Files
import cats.effect.IO
import org.scalatest.{BeforeAndAfterAll, AsyncFunSuite}
import scala.concurrent.Future

class InotifyWaitIterantSpec extends AsyncFunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF =
      Future(
        InotifyWaitIterant[IO](testDir, false, Set()).take(4).toListL.unsafeRunSync()
      )

    val expectedEvents = InotifyWaitSpec.createEvents(tempFile)

    for (result <- resultF) yield {
      assertResult(expectedEvents)(result)
    }
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
