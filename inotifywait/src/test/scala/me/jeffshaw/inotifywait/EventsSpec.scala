package me.jeffshaw.inotifywait

import java.nio.file.Paths
import org.scalatest.FunSuite

class EventsSpec extends FunSuite {

  def testEvents(
    name: String,
    line: String,
    expected: Events
  ): Unit = {
    test(name) {
      val actual = Events.valueOf(line)
      assertResult(expected)(actual)
    }
  }

  testEvents("watching directory", "/dir,ISDIR,", Events(Set(Event.ISDIR), Paths.get("/dir")))
  testEvents("watching file", "file,CREATE,", Events(Set(Event.CREATE), Paths.get("file")))

}
