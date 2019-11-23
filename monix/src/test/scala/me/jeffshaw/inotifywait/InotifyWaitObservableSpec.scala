package me.jeffshaw.inotifywait

import java.nio.file.Files
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import monix.execution.Scheduler.Implicits.global

class InotifyWaitObservableSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitObservableSpec")

  test("observable") {
    val testDir = suiteDir.resolve("observable")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    Thread.sleep(2000L)

    var expectedEvents: Seq[Events] = null

    val events =
      for {
        process <- InotifyWaitObservable.start(testDir, false, Set())
        () = Thread.sleep(2000L)
        () = expectedEvents = InotifyWaitSpec.createEvents(tempFile)
        // We don't get 4 events unless we create > 4 events.
        _ = InotifyWaitSpec.createEvents(tempFile)
        events <- InotifyWaitObservable.toEvents(process)
      } yield events

    val actual = events.take(4).toListL.runSyncUnsafe()
    assertResult(expectedEvents)(actual)
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
