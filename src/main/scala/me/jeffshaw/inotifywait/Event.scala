package me.jeffshaw.inotifywait

import fastparse.Parsed.{Failure, Success}
import fastparse.parse
import java.nio.file.{Path, Paths}

case class Event(
  events: Set[Event.Type],
  path: Path
)

object Event {
  val lineMatcher = "(\")?[^\"]+\\g{1},{2}(\")?[^\"]+\\g{1}"

  def valueOf(line: String): Event = {
    parse(line, Csv.csv(_)) match {
      case Success(Seq(watchedFile, eventStrings, eventFile), _) =>
        val events = {
          for (name <- eventStrings.split(",")) yield {
            Type.byName.getOrElse(name, throw new IllegalArgumentException("unknown event type " + name))
          }
        }.toSet
        Event(events, Paths.get(watchedFile, eventFile))
      case f: Failure =>
        throw new RuntimeException("event parsing failed for " + line + " at index " + f.index)
    }
  }

  // https://github.com/rvoicilas/inotify-tools/blob/501a806c9fa2cccb90ee55b7d36c00a78fac5527/libinotifytools/src/inotifytools.c#L564
  sealed abstract class Type(val name: String)

  object Type {
    val byName: Map[String, Type] =
      Map(
        ACCESS.name -> ACCESS,
        MODIFY.name -> MODIFY,
        ATTRIB.name -> ATTRIB,
        CLOSE_WRITE.name -> CLOSE_WRITE,
        CLOSE_NOWRITE.name -> CLOSE_NOWRITE,
        OPEN.name -> OPEN,
        MOVED_FROM.name -> MOVED_FROM,
        MOVED_TO.name -> MOVED_TO,
        CREATE.name -> CREATE,
        DELETE.name -> DELETE,
        DELETE_SELF.name -> DELETE_SELF,
        UNMOUNT.name -> UNMOUNT,
        Q_OVERFLOW.name -> Q_OVERFLOW,
        IGNORED.name -> IGNORED,
        CLOSE.name -> CLOSE,
        MOVE_SELF.name -> MOVE_SELF,
        ISDIR.name -> ISDIR,
        ONESHOT.name -> ONESHOT
      )

    case object ACCESS extends Type("ACCESS")
    case object MODIFY extends Type("MODIFY")
    case object ATTRIB extends Type("ATTRIB")
    case object CLOSE_WRITE extends Type("CLOSE_WRITE")
    case object CLOSE_NOWRITE extends Type("CLOSE_NOWRITE")
    case object OPEN extends Type("OPEN")
    case object MOVED_FROM extends Type("MOVED_FROM")
    case object MOVED_TO extends Type("MOVED_TO")
    case object CREATE extends Type("CREATE")
    case object DELETE extends Type("DELETE")
    case object DELETE_SELF extends Type("DELETE_SELF")
    case object UNMOUNT extends Type("UNMOUNT")
    case object Q_OVERFLOW extends Type("Q_OVERFLOW")
    case object IGNORED extends Type("IGNORED")
    case object CLOSE extends Type("CLOSE")
    case object MOVE_SELF extends Type("MOVE_SELF")
    case object ISDIR extends Type("ISDIR")
    case object ONESHOT extends Type("ONESHOT")
  }
}
