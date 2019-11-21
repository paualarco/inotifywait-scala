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

licenses += ("Apache", url("https://www.apache.org/licenses/LICENSE-2.0"))

homepage := Some(url("https://github.com/shawjef3/inotifywait-scala"))

pomExtra :=
  <developers>
    <developer>
      <name>Jeff Shaw</name>
      <id>shawjef3</id>
      <url>https://github.com/shawjef3/</url>
    </developer>
  </developers>
    <scm>
      <url>git@github.com:shawjef3/inotifywait-scala.git</url>
      <connection>scm:git:git@github.com:shawjef3/inotifywait-scala.git</connection>
    </scm>

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true
