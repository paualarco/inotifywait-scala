package me.jeffshaw.inotifywait

import cats.effect.Sync
import java.nio.file.Path
import monix.tail.Iterant

object InotifyWaitIterant {
  def apply[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F],
    codec: io.Codec
  ): Iterant[F, Events] = {
    val processI =
      Iterant.resource(
        F.delay(new ProcessBuilder(InotifyWait.command(path, recursive, subscriptions): _*).start())
      )(process => F.delay {
        process.destroy()
        process.waitFor()
      })
    for {
      process <- processI
      line <- Iterant.fromIterator(io.Source.fromInputStream(process.getInputStream).getLines())
    } yield Events.valueOf(line)
  }
}
