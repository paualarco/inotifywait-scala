package me.jeffshaw.inotifywait

// https://github.com/rvoicilas/inotify-tools/blob/501a806c9fa2cccb90ee55b7d36c00a78fac5527/libinotifytools/src/inotifytools.c#L566
sealed trait Subscription

object Subscription {

  case object ACCESS extends Subscription
  case object MODIFY extends Subscription
  case object ATTRIB extends Subscription
  case object CLOSE_WRITE extends Subscription
  case object CLOSE_NOWRITE extends Subscription
  case object OPEN extends Subscription
  case object MOVED_FROM extends Subscription
  case object MOVED_TO extends Subscription
  case object CREATE extends Subscription
  case object DELETE extends Subscription
  case object DELETE_SELF extends Subscription
  case object UNMOUNT extends Subscription
  case object Q_OVERFLOW extends Subscription
  case object IGNORED extends Subscription
  case object CLOSE extends Subscription
  case object MOVE_SELF extends Subscription
  case object MOVE extends Subscription
  case object ISDIR extends Subscription
  case object ONESHOT extends Subscription
  case object ALL_EVENTS extends Subscription

}
