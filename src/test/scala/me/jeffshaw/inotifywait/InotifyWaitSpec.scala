package me.jeffshaw.inotifywait

import java.nio.file.Files
import org.scalatest.FunSuite
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Await
import scala.concurrent.duration._

class InotifyWaitSpec extends FunSuite {

  val temp = Files.createTempDirectory("InotifyWaitSpec")

  test("create") {
    val tempFile = temp.resolve("create")
    val eventStream = InotifyWait.run(temp.toString, false, None)

    // wait for inotifywait to start
    Thread.sleep(1000L)

    // create some events
    Files.createFile(tempFile)
    Files.delete(tempFile)

    val actual = Await.result(eventStream, 3 seconds).take(4).toVector

    val expected =
      Vector(
        Event(temp.toString + "/", Set(Event.Type.CREATE), "create"),
        Event(temp.toString + "/", Set(Event.Type.OPEN), "create"),
        Event(temp.toString + "/", Set(Event.Type.CLOSE_WRITE, Event.Type.CLOSE), "create"),
        Event(temp.toString + "/", Set(Event.Type.DELETE), "create")
      )

    assertResult(expected)(actual)
  }

}
