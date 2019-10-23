package me.jeffshaw.inotifywait

import fastparse._
import fastparse.NoWhitespace._

/**
  * Parser for lines given by inotifywait.
  */
object Csv {

  def quotedFieldValue[_: P]: P[String] = P {
    ("\"" ~ "\"".! |
      !"\"" ~ AnyChar.!
    ).rep.map(_.mkString)
  }

  def quotedField[_: P]: P[String] = P("\"" ~/ quotedFieldValue ~ "\"")

  // for unit testing
  private[inotifywait] def onlyQuotedField[_: P]: P[String] = P(quotedField ~ End)

  def field[_: P]: P[String] = P((!CharIn("\",") ~ AnyChar.!).rep).map(_.mkString)

  def csv[_: P]: P[Seq[String]] = P(Start ~ (quotedField | field).rep(sep = P(",")) ~ End)

}
