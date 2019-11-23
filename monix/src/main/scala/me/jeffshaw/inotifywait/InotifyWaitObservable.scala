package me.jeffshaw.inotifywait

import java.nio.file.Path
import cats.effect.Resource
import monix.eval.Task
import monix.reactive.Observable

object InotifyWaitObservable {
  // factored out for testing
  def start(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit codec: io.Codec
  ): Observable[Process] = {
    Observable.fromResource[Task, Process](
      Resource[Task, Process](Task.delay {
        val process = new ProcessBuilder(InotifyWait.command(path, recursive, subscriptions): _*).start()
        val close = Task.delay[Unit] {
          process.destroy()
          process.waitFor()
        }
        (process, close)
      })
    )
  }

  def toEvents(
    p: Process
  )(implicit codec: io.Codec
  ): Observable[Events] = {
    Observable.fromIterator(Task.now(io.Source.fromInputStream(p.getInputStream).getLines().map(Events.valueOf)))
  }

  def apply(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit codec: io.Codec
  ): Observable[Events] = {
    for {
      process <- start(path, recursive, subscriptions)
      event <- toEvents(process)
    } yield event
  }
}
