package me.jeffshaw.inotifywait

import java.io.{BufferedReader, InputStreamReader}

object InotifyWait {

  val commandPrefix: Seq[String] = Seq("inotifywait", "-c", "-q", "-m")

  def createCommand(
    path: String,
    recursive: Boolean,
    events: Set[Event.Type]
  ): Seq[String] = {
    val eventsSeq =
      if (events.isEmpty) Seq.empty
      else Seq(events.mkString("-e", ",", " "))

    val recursiveSeq =
      if (recursive) Seq("-r")
      else Seq.empty

    commandPrefix ++
      recursiveSeq ++
      eventsSeq :+
      path
  }

  def createProcess(
    path: String,
    recursive: Boolean,
    events: Set[Event.Type]
  ): ProcessBuilder = {
    val command = createCommand(path, recursive, events)
    new ProcessBuilder(command: _*)
  }

  /**
    * Read events from the given process until it exits.
    * Returns a thunk because otherwise `getEvents` would block.
    * The stream will only be created once for multiple calls
    * of the thunk.
    */
  def getEvents(p: java.lang.Process): () => Stream[Event] = {
    val in = new BufferedReader(new InputStreamReader(p.getInputStream))
    // only allow one instance of the stream
    lazy val events = Stream.continually(in.readLine()).takeWhile(_ != null).map(Event.valueOf)
    () => events
  }

}
