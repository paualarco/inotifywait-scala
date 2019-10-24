package me.jeffshaw.inotifywait

// https://github.com/rvoicilas/inotify-tools/blob/501a806c9fa2cccb90ee55b7d36c00a78fac5527/libinotifytools/src/inotifytools.c#L564
sealed abstract class Event(val name: String)

object Event {
  val byName: Map[String, Event] =
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

  case object ACCESS extends Event("ACCESS")
  case object MODIFY extends Event("MODIFY")
  case object ATTRIB extends Event("ATTRIB")
  case object CLOSE_WRITE extends Event("CLOSE_WRITE")
  case object CLOSE_NOWRITE extends Event("CLOSE_NOWRITE")
  case object OPEN extends Event("OPEN")
  case object MOVED_FROM extends Event("MOVED_FROM")
  case object MOVED_TO extends Event("MOVED_TO")
  case object CREATE extends Event("CREATE")
  case object DELETE extends Event("DELETE")
  case object DELETE_SELF extends Event("DELETE_SELF")
  case object UNMOUNT extends Event("UNMOUNT")
  case object Q_OVERFLOW extends Event("Q_OVERFLOW")
  case object IGNORED extends Event("IGNORED")
  case object CLOSE extends Event("CLOSE")
  case object MOVE_SELF extends Event("MOVE_SELF")
  case object ISDIR extends Event("ISDIR")
  case object ONESHOT extends Event("ONESHOT")
}
