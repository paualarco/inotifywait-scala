package me.jeffshaw.inotifywait

import java.nio.file.Path
import cats.effect.Resource
import monix.eval.Task
import monix.reactive.Observable

object InotifyWaitObservable {
  def apply[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit codec: io.Codec
  ): Observable[Events] = {
    Observable.fromIterator(
      Resource[Task, Iterator[Events]](Task.delay {
        val process = new ProcessBuilder(InotifyWait.command(path, recursive, subscriptions): _*).start()
        val lines = io.Source.fromInputStream(process.getInputStream).getLines().map(Events.valueOf)
        val close = Task.delay[Unit] {
          process.destroy()
          process.waitFor()
        }
        (lines, close)
      })
    )
  }
}
