lazy val root = project.in(file(".")).aggregate(inotifywait, fs2)
  .settings(update/aggregate := false)

lazy val inotifywait = project.in(file("inotifywait"))

lazy val fs2 = project.in(file("fs2"))
  .dependsOn(inotifywait % "compile->compile;test->test")

lazy val monix = project.in(file("monix"))
  .dependsOn(inotifywait % "compile->compile;test->test")
