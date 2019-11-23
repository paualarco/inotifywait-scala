package me.jeffshaw.inotifywait

import cats.effect.IO
import java.nio.file.Files
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll}
import scala.concurrent.{ExecutionContext, Future}

class InotifyWaitFs2Spec extends AsyncFunSuite with BeforeAndAfterAll {
  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  override implicit def executionContext: ExecutionContext = {
    scala.concurrent.ExecutionContext.Implicits.global
  }

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF = Future(InotifyWaitFs2.start[IO](testDir, false, Set()).take(4).compile.toList.unsafeRunSync())

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