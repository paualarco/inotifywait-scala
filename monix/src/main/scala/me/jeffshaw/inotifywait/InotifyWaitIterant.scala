package me.jeffshaw.inotifywait

import cats.effect.Sync
import java.nio.file.Path
import monix.tail.Iterant

object InotifyWaitIterant {
  def acquire[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F],
    codec: io.Codec
  ): Iterant[F, Process] = {
    Iterant.resource(
      F.delay(new ProcessBuilder(InotifyWait.command(path, recursive, subscriptions): _*).start())
    )(process => F.delay {
      process.destroy()
      process.waitFor()
    })
  }

  def toEvents[F[_]](process: Process)(implicit F: Sync[F]): Iterant[F, Events] = {
    Iterant.fromIterator(io.Source.fromInputStream(process.getInputStream).getLines().map(Events.valueOf))
  }

  def start[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F],
    codec: io.Codec
  ): Iterant[F, Events] = {
    for {
      process <- acquire(path, recursive, subscriptions)
      event <- toEvents(process)
    } yield event
  }
}
