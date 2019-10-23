package me.jeffshaw.inotifywait

import fastparse._
import org.scalatest.FunSuite

class CsvSpec extends FunSuite {

  test("quotedField") {
    val actual = parse("\"hi\"", Csv.quotedField(_))
    val expected = Parsed.Success("hi", 4)
    assertResult(expected)(actual)
  }

  test("quotedField with double quotes") {
    val actual = parse("\"h\"\"i\"", Csv.quotedField(_))
    val expected = Parsed.Success("h\"i", 6)
    assertResult(expected)(actual)
  }

  test("quotedField with single quote") {
    val actual = parse("\"h\"i\"", Csv.onlyQuotedField(_))
    assert(actual.isInstanceOf[Parsed.Failure])
  }

  test("csv one field") {
    val actual = parse("hi", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi"), 2)
    assertResult(expected)(actual)
  }

  test("csv one quoted field") {
    val actual = parse("\"hi\"", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi"), 4)
    assertResult(expected)(actual)
  }

  test("csv1") {
    val actual = parse("""hi""", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi"), 2)
    assertResult(expected)(actual)
  }

  test("csv2") {
    val actual = parse("""hi,"there"""", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi", "there"), 10)
    assertResult(expected)(actual)
  }

  test("csv3") {
    val actual = parse("""hi,"there",","""", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi", "there", ","), 14)
    assertResult(expected)(actual)
  }

  test("csv4") {
    val actual = parse("""hi,"there",",","you""are"""", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi", "there", ",", "you\"are"), 25)
    assertResult(expected)(actual)
  }

  test("csv5") {
    val actual = parse("""hi,"there",",","you""are",good""", Csv.csv(_))
    val expected = Parsed.Success(Seq("hi", "there", ",", "you\"are", "good"), 30)
    assertResult(expected)(actual)
  }

  test("./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"") {
    val actual = parse("./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"", Csv.csv(_))
    val expected = Parsed.Success(Seq("./", "CLOSE_WRITE,CLOSE", "\"hi"), 29)
    assertResult(expected)(actual)
  }

}
