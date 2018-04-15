package me.jeffshaw.inotifywait

import fastparse.core.Parsed.{Failure, Success}

case class Event(
  watchedFile: String,
  events: Set[Event.Type],
  eventFile: String
)

object Event {
  val lineMatcher = "(\")?[^\"]+\\g{1},{2}(\")?[^\"]+\\g{1}"

  def valueOf(line: String): Event = {
    Csv.csv.parse(line) match {
      case Success(Seq(watchedFile, eventStrings, eventFile), _) =>
        val events = eventStrings.split(",").map(Event.Type.valueOf).toSet
        Event(watchedFile, events, eventFile)
      case f: Failure[_, _] =>
        throw new RuntimeException("event parsing failed for " + line + " at index " + f.index)
    }

  }

  sealed abstract class Type(val name: String)

  object Type {
    def valueOf(asString: String): Type = {
      asString match {
        case ACCESS.name => ACCESS
        case MODIFY.name => MODIFY
        case ATTRIB.name => ATTRIB
        case CLOSE_WRITE.name => CLOSE_WRITE
        case CLOSE_NOWRITE.name => CLOSE_NOWRITE
        case CLOSE.name => CLOSE
        case OPEN.name => OPEN
        case MOVED_TO.name => MOVED_TO
        case MOVED_FROM.name => MOVED_FROM
        case MOVE.name => MOVE
        case CREATE.name => CREATE
        case DELETE.name => DELETE
        case DELETE_SELF.name => DELETE_SELF
        case UNMOUNT.name => UNMOUNT
        case _ =>
          throw new IllegalArgumentException("unknown event type " + asString)
      }
    }

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
