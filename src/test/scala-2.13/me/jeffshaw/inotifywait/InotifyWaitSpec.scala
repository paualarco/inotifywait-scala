package me.jeffshaw.inotifywait

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class InotifyWaitSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitSpec")

  test("createProcess") {
    val testDir = suiteDir.resolve("createProcess")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")
    val builder = InotifyWait.createProcess(testDir.toString, false, Set())


    val process = builder.start()
    val eventStream = InotifyWait.getEvents(process)

    // create some events
    Files.createFile(tempFile)
    Files.delete(tempFile)

    val events = eventStream.take(4).toVector

    val expected =
      Vector(
        Event(testDir.toString + "/", Set(Event.Type.CREATE), "file"),
        Event(testDir.toString + "/", Set(Event.Type.OPEN), "file"),
        Event(testDir.toString + "/", Set(Event.Type.CLOSE_WRITE, Event.Type.CLOSE), "file"),
        Event(testDir.toString + "/", Set(Event.Type.DELETE), "file")
      )

    assertResult(expected)(events)
    process.destroy()
    process.waitFor()
  }

  override protected def afterAll(): Unit = {
    Files.walkFileTree(
      suiteDir,
      new SimpleFileVisitor[Path]() {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          Files.delete(file)
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
          Files.delete(dir)
          FileVisitResult.CONTINUE
        }
      }
    )
  }

}
