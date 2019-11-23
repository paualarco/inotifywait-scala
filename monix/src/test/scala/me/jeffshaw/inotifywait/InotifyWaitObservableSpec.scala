package me.jeffshaw.inotifywait

import java.nio.file.Files
import monix.eval.Task
import org.scalatest.{BeforeAndAfterAll, AsyncFunSuite}
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.duration._

class InotifyWaitObservableSpec extends AsyncFunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitObservableSpec")

  test("observable") {
    val testDir = suiteDir.resolve("observable")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val events =
      Task.parZip2(
        InotifyWaitObservable.start(testDir, false, Set()).take(4).toListL,
        Task.delay {
          InotifyWaitSpec.createEvents(tempFile)
          // For some reason we need > 4 events to get `take(4)` to actually get 4 events.
          InotifyWaitSpec.createEvents(tempFile)
        }.delayExecution(1.seconds)
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
