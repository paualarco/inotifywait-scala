package me.jeffshaw.inotifywait

import fastparse.Parsed.{Failure, Success}
import fastparse.parse

case class Event(
  watchedFile: String,
  events: Set[Event.Type],
  eventFile: String
)

object Event {
  val lineMatcher = "(\")?[^\"]+\\g{1},{2}(\")?[^\"]+\\g{1}"

  def valueOf(line: String): Event = {
    parse(line, Csv.csv(_)) match {
      case Success(Seq(watchedFile, eventStrings, eventFile), _) =>
        val events = {
          for (name <- eventStrings.split(",")) yield {
            Event.Type.byName.getOrElse(name, throw new IllegalArgumentException("unknown event type " + name))
          }
        }.toSet
        Event(watchedFile, events, eventFile)
      case f: Failure =>
        throw new RuntimeException("event parsing failed for " + line + " at index " + f.index)
    }
  }

  sealed abstract class Type(val name: String)

  object Type {
    val byName: Map[String, Type] =
      Map(
        ACCESS.name -> ACCESS,
        MODIFY.name -> MODIFY,
        ATTRIB.name -> ATTRIB,
        CLOSE_WRITE.name -> CLOSE_WRITE,
        CLOSE_NOWRITE.name -> CLOSE_NOWRITE,
        CLOSE.name -> CLOSE,
        OPEN.name -> OPEN,
        MOVED_TO.name -> MOVED_TO,
        MOVED_FROM.name -> MOVED_FROM,
        MOVE.name -> MOVE,
        CREATE.name -> CREATE,
        DELETE.name -> DELETE,
        DELETE_SELF.name -> DELETE_SELF,
        UNMOUNT.name -> UNMOUNT
      )

    case object ACCESS extends Type("ACCESS")
    case object MODIFY extends Type("MODIFY")
    case object ATTRIB extends Type("ATTRIB")
    case object CLOSE_WRITE extends Type("CLOSE_WRITE")
    case object CLOSE_NOWRITE extends Type("CLOSE_NOWRITE")
    case object CLOSE extends Type("CLOSE")
    case object OPEN extends Type("OPEN")
    case object MOVED_TO extends Type("MOVED_TO")
    case object MOVED_FROM extends Type("MOVED_FROM")
    case object MOVE extends Type("MOVE")
    case object CREATE extends Type("CREATE")
    case object DELETE extends Type("DELETE")
    case object DELETE_SELF extends Type("DELETE_SELF")
    case object UNMOUNT extends Type("UNMOUNT")
  }
}
