package me.jeffshaw.inotifywait

import cats.effect.IO
import java.nio.file.Files
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll}
import scala.concurrent.{ExecutionContext, Future, Promise}

class InotifyWaitFs2Spec extends AsyncFunSuite with BeforeAndAfterAll {
  val suiteDir = Files.createTempDirectory("InotifyWaitFs2Spec")

  override implicit def executionContext: ExecutionContext = ExecutionContext.global

  test("fs2") {
    val testDir = suiteDir.resolve("fs2")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF: Future[List[Events]] = {
      val p = Promise[List[Events]]()
      Future(InotifyWaitFs2.start[IO](testDir, false, Set()).take(4).compile.toList.unsafeRunAsync {
        case Left(e) => p.failure(e)
        case Right(events) => p.success(events)
      })
      p.future
    }

    // This is required, or else the test hangs waiting for events.
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
