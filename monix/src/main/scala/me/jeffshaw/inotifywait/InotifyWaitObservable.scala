package me.jeffshaw.inotifywait

import java.nio.file.Path
import monix.eval.Task
import monix.reactive.Observable

object InotifyWaitObservable {
  def toObservable(
    i: InotifyWait
  ): Observable[Events] = {
    Observable.fromIterator(Task.pure(i.events))
  }

  def acquire(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  ): Observable[InotifyWait] = {
    Observable.resource(
      Task(InotifyWait.start(path, recursive, subscriptions))
    )(i => Task {
      i.process.destroy()
      i.process.waitFor()
    })
  }

  def start(
    path: Path,
    recursive: Boolean,
    subscriptions: Set[Subscription]
  ): Observable[Events] = {
    acquire(path,recursive, subscriptions).flatMap(toObservable)
  }
}
