package me.jeffshaw.inotifywait

import java.io.IOException
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class InotifyWaitSpec extends FunSuite with BeforeAndAfterAll {

  val suiteDir = Files.createTempDirectory("InotifyWaitSpec")

  test("createProcess") {
    val testDir = suiteDir.resolve("createProcess")
    Files.createDirectories(testDir)
    val tempFile = testDir.resolve("file")
    val process = InotifyWait.start(testDir, false, Set())

    // create some events
    Files.createFile(tempFile)
    Files.delete(tempFile)

    val events = process.events.take(4).toVector

    val expected =
      Vector(
        Event(Set(Event.Type.CREATE), tempFile),
        Event(Set(Event.Type.OPEN), tempFile),
        Event(Set(Event.Type.CLOSE_WRITE, Event.Type.CLOSE), tempFile),
        Event(Set(Event.Type.DELETE), tempFile)
      )

    assertResult(expected)(events)
    process.process.destroy()
    process.process.waitFor()
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
