package me.jeffshaw.inotifywait

import cats.effect.Sync
import fs2._
import java.nio.file.Path

object InotifyWaitFs2 {
  def toStream[F[_]](
    i: InotifyWait
  )(implicit F: Sync[F]
  ): Stream[F, Events] = {
    Stream.fromIterator[F](i.events)
  }

  def acquire[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F]
  ): Stream[F, InotifyWait] = {
    Stream.bracket(F.delay(InotifyWait.start(path, recursive, subscriptions))){
      i => F.delay {
        i.process.destroy()
      }
    }
  }

  def start[F[_]](
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  )(implicit F: Sync[F]
  ): Stream[F, Events] = {
    for {
      process <- acquire(path, recursive, subscriptions)
      event <- toStream(process)
    } yield event
  }
}
