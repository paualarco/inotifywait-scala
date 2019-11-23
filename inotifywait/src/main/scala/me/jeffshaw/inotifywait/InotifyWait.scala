package me.jeffshaw.inotifywait

import java.nio.file.Path
import scala.io.Codec

class InotifyWait private (
  val path: Path,
  val recursive: Boolean,
  val subscriptions: Set[Subscription],
  val process: Process,
  val events: Iterator[Events]
) {
  def startDuplicate(
    path: Path = path,
    recursive: Boolean = recursive,
    subscriptions: Set[Subscription] = subscriptions
  ): InotifyWait = {
    InotifyWait.start(path, recursive, subscriptions)
  }
}

object InotifyWait {
  def command(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  ): Seq[String] = {
    val subscriptionsSeq =
      if (subscriptions.isEmpty) Seq.empty
      else Seq(subscriptions.mkString("-e", ",", ""))

    val recursiveSeq =
      if (recursive) Seq("-r")
      else Seq.empty

    InotifyWait.commandPrefix ++
      recursiveSeq ++
      subscriptionsSeq :+
      // Use the full path to avoid "./" being given by inotifywait as the watched dir, which will fail Paths.get.
      path.toAbsolutePath.toString
  }

  def start(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit codec: Codec
  ): InotifyWait = {
    val process = new ProcessBuilder(command(path, recursive, subscriptions): _*).start()
    val in = io.Source.fromInputStream(process.getInputStream)
    val events = in.getLines().map(Events.valueOf)
    new InotifyWait(path, recursive, subscriptions, process, events)
  }

  val commandPrefix: Seq[String] = Seq("inotifywait", "-c", "-q", "-m")

}
