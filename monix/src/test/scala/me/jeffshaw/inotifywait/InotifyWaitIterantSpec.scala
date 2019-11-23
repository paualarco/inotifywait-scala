package me.jeffshaw.inotifywait

import java.nio.file.Files
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{AsyncFunSuite, BeforeAndAfterAll}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class InotifyWaitIterantSpec extends AsyncFunSuite with BeforeAndAfterAll {

  override implicit def executionContext: ExecutionContext = ExecutionContext.global

  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val events =
      Task.parZip2(
        InotifyWaitIterant.start[Task](testDir, false, Set()).take(4).toListL,
        Task.delay(InotifyWaitSpec.createEvents(tempFile)).delayExecution(1.second)
      )

    for {
      (actual, expected) <- events.runToFuture
    } yield {
      assertResult(expected)(actual)
    }
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
