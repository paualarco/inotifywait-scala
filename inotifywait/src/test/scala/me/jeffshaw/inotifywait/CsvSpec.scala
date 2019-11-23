package me.jeffshaw.inotifywait

import fastparse._
import org.scalactic.source.Position
import org.scalatest.FunSuite

class CsvSpec extends FunSuite {

  def testParser[A](
    name: String,
    input: String,
    parser: P[_] => P[A],
    assertion: Parsed[A] => Any
  )(implicit pos: Position
  ): Unit = {
    test(name) {
      val actual = parse(input, parser)
      assertion(actual)
    }
  }

  testParser("quotedField", "\"hi\"", Csv.quotedField(_), assertResult(Parsed.Success("hi", 4)))
  testParser("quotedField with double quotes", "\"h\"\"i\"", Csv.quotedField(_), assertResult(Parsed.Success("h\"i", 6)))
  testParser("quotedField with single quote", "\"h\"i\"", Csv.onlyQuotedField(_), (result: Parsed[String]) => result.isInstanceOf[Parsed.Failure])
  testParser("csv one field", "hi", Csv.csv(_), assertResult(Parsed.Success(Seq("hi"), 2)))
  testParser("csv one quoted field", "\"hi\"", Csv.csv(_), assertResult(Parsed.Success(Seq("hi"), 4)))
  testParser("csv1", """hi""", Csv.csv(_), assertResult(Parsed.Success(Seq("hi"), 2)))
  testParser("csv2", """hi,"there"""", Csv.csv(_), assertResult(Parsed.Success(Seq("hi", "there"), 10)))
  testParser("csv3", """hi,"there",","""", Csv.csv(_), assertResult(Parsed.Success(Seq("hi", "there", ","), 14)))
  testParser("csv4", """hi,"there",",","you""are"""", Csv.csv(_), assertResult(Parsed.Success(Seq("hi", "there", ",", "you\"are"), 25)))
  testParser("csv5", """hi,"there",",","you""are",good""", Csv.csv(_), assertResult(Parsed.Success(Seq("hi", "there", ",", "you\"are", "good"), 30)))
  testParser("./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"", "./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"", Csv.csv(_), assertResult(Parsed.Success(Seq("./", "CLOSE_WRITE,CLOSE", "\"hi"), 29)))
  testParser("watching directory", "/,IS_DIR,", Csv.csv(_), assertResult(Parsed.Success(Seq("/", "ISDIR", ""), 9)))

}
