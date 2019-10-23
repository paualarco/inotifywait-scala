organization := "me.jeffshaw.inotifywait"

name := "inotifywait"

version := "2.0"

scalaVersion := "2.13.1"

crossScalaVersions := Seq(
  "2.12.10",
  "2.11.12"
)

def fastParseVersion(scalaVersion: String): String =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2,11)) => "2.1.2"
    case _ => "2.1.3"
  }

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % fastParseVersion(scalaVersion.value),
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)
