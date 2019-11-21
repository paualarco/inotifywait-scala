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
    val expected = InotifyWaitSpec.createEvents(tempFile)

    val events = process.events.take(4).toVector

    assertResult(expected)(events)
    process.process.destroy()
    process.process.waitFor()
  }

  override protected def afterAll(): Unit = {
    InotifyWaitSpec.clean(suiteDir)
  }

}

object InotifyWaitSpec {
  def clean(path: Path): Unit = {
    Files.walkFileTree(
      path,
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

  /**
    * Create and delete the given file.
    * @param path
    * @return the expected events
    */
  def createEvents(path: Path): Vector[Events] = {
    Files.createFile(path)
    Files.delete(path)

    Vector(
      Events(Set(Event.CREATE), path),
      Events(Set(Event.OPEN), path),
      Events(Set(Event.CLOSE_WRITE, Event.CLOSE), path),
      Events(Set(Event.DELETE), path)
    )
  }
}
