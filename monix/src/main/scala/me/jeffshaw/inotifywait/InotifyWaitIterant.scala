package me.jeffshaw.inotifywait

import cats.Applicative
import cats.effect.Sync
import java.nio.file.Path
import monix.tail.Iterant

object InotifyWaitIterant {
  def toIterant[F[_]](
    i: InotifyWait
  )(implicit F: Applicative[F]
  ): Iterant[F, Events] = {
    Iterant.fromIterator(i.events)
  }

  def acquire[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F]
  ): Iterant[F, InotifyWait] = {
    Iterant.resource(
      F.delay(InotifyWait.start(path, recursive, subscriptions))
    )(i => F.delay {
      i.process.destroy()
      i.process.waitFor()
    })
  }

  def start[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F]
  ): Iterant[F, Events] = {
    acquire(path,recursive, subscriptions).flatMap(toIterant)
  }
}
