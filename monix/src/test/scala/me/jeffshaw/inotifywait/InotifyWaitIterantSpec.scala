package me.jeffshaw.inotifywait

import java.nio.file.Files
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{FunSuite, BeforeAndAfterAll}
import scala.concurrent.duration._

class InotifyWaitIterantSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitIterantSpec")

  test("iterant") {
    val testDir = suiteDir.resolve("iterant")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")

    Task.parMap2(
      InotifyWaitIterant.start[Task](testDir, false, Set()).take(4).toListL,
      Task.delay(InotifyWaitSpec.createEvents(tempFile)).delayExecution(1.second)
    ) { (actual, expected) =>
        assertResult(expected)(actual)
    }.runSyncUnsafe()
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}
