package me.jeffshaw.inotifywait

import scala.concurrent._
import scala.sys.process._

object InotifyWait {

  def createCommand(
    path: String,
    recursive: Boolean,
    events: Option[Set[Event.Type]]
  ): String = {
    val eventsString =
      events.map(_.mkString("-e", ",", " ")).getOrElse("")

    val recursiveString =
      if (recursive) "-r " else ""

    "inotifywait -c -q -m " +
      recursiveString +
      eventsString +
      path
  }

  /**
    * Run inotifywait, but do not throw an exception if it fails.
    * @return
    */
  def run_!(
    path: String,
    recursive: Boolean,
    events: Option[Set[Event.Type]]
  )(implicit ec: ExecutionContext
  ): Future[Stream[Event]] = {
    Future {
      blocking {
        createCommand(path, recursive, events).lineStream_!.map(Event.valueOf)
      }
    }
  }

  /**
    * Run inotifywait, and throw an exception if it fails.
    * @return
    */
  def run(
    path: String,
    recursive: Boolean,
    events: Option[Set[Event.Type]]
  )(implicit ec: ExecutionContext
  ): Future[Stream[Event]] = {
    Future {
      blocking {
        createCommand(path, recursive, events).lineStream.map(Event.valueOf)
      }
    }
  }

}
