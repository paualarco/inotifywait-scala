package me.jeffshaw.inotifywait

import java.nio.file.Files
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import monix.execution.Scheduler.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class InotifyWaitObservableSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitObservableSpec")

  test("observable") {
    val testDir = suiteDir.resolve("observable")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    val resultF = InotifyWaitObservable.start(testDir, false, Set()).take(4).toListL.runToFuture

    val expectedEvents = InotifyWaitSpec.createEvents(tempFile)

    val result = Await.result(resultF, Duration.Inf)

    assertResult(expectedEvents)(result)
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
