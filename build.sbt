organization := "me.jeffshaw.inotifywait"

name := "inotifywait"

version := "1.0"

scalaVersion := "2.12.5"

crossScalaVersions := Seq(
  "2.11.12",
  "2.10.7"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" % "fastparse_2.12" % "1.0.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)
