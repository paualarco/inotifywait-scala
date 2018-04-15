package me.jeffshaw.inotifywait

import fastparse.all._

/**
  * Parser for lines given by inotifywait.
  */
object Csv {

  val quotedFieldValue: P[String] = P {
    "\"" ~ "\"".! |
      !"\"" ~ AnyChar.!
  }.rep.map(_.mkString)

  val quotedField: P[String] = P {
    "\"" ~/ quotedFieldValue ~ "\""
  }

  val field: P[String] = P((!CharIn(Seq('"', ',')) ~ AnyChar.!).rep).map(_.mkString)

  val csv = P(Start ~ (quotedField | field).rep(sep = P(",")) ~ End)

}
