package me.jeffshaw.inotifywait

import fastparse.Parsed.{Failure, Success}
import fastparse.parse
import java.nio.file.{Path, Paths}

case class Events(
  events: Set[Event],
  path: Path
)

object Events {
  val lineMatcher = "(\")?[^\"]+\\g{1},{2}(\")?[^\"]+\\g{1}"

  def valueOf(line: String): Events = {
    parse(line, Csv.csv(_)) match {
      case Success(Seq(watchedFile, eventStrings, eventFile), _) =>
        val events = {
          for (name <- eventStrings.split(",")) yield {
            Event.byName.getOrElse(name, throw new IllegalArgumentException("unknown event name " + name))
          }
        }.toSet
        Events(events, Paths.get(watchedFile, eventFile))
      case Success(values, _) =>
        throw new InotifyWaitException(s"event parsing failed for $line. Expected 3 values but got ${values.size}.")
      case f: Failure =>
        throw new InotifyWaitException(s"event parsing failed for $line at index ${f.index}")
    }
  }
}
