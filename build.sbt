organization := "me.jeffshaw.inotifywait"

name := "inotifywait"

version := "2.0"

scalaVersion := "2.13.1"

crossScalaVersions := Seq(
  "2.12.10",
  "2.11.12"
)

unmanagedSourceDirectories in Compile += (sourceDirectory in Compile).value / (
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => "scala-2.13"
    case Some((2, 11 | 12)) => "scala-2.1x"
  })

unmanagedSourceDirectories in Test += (sourceDirectory in Test).value / (
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => "scala-2.13"
    case Some((2, 11 | 12)) => "scala-2.1x"
  })

def fastParseVersion(scalaVersion: String): String =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2,11)) => "2.1.2"
    case _ => "2.1.3"
  }

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % fastParseVersion(scalaVersion.value),
  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)
