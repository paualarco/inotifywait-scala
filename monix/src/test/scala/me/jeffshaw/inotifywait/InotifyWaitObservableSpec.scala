package me.jeffshaw.inotifywait

import java.nio.file.Files
import org.scalatest.{BeforeAndAfterAll, AsyncFunSuite}
import monix.execution.Scheduler.Implicits.global

class InotifyWaitObservableSpec extends AsyncFunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitObservableSpec")

  test("observable") {
    val testDir = suiteDir.resolve("observable")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF = InotifyWaitObservable(testDir, false, Set()).take(4).toListL.runToFuture

    val expectedEvents = InotifyWaitSpec.createEvents(tempFile)

    for (result <- resultF) yield {
      assertResult(expectedEvents)(result)
    }
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
