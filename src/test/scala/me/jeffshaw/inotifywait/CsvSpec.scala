package me.jeffshaw.inotifywait

import fastparse.all._
import org.scalatest.FunSuite

class CsvSpec extends FunSuite {

  test("quotedField") {
    val actual = Csv.quotedField.parse("\"hi\"")
    val expected = Parsed.Success("hi", 4)
    assertResult(expected)(actual)
  }

  test("quotedField with double quotes") {
    val actual = Csv.quotedField.parse("\"h\"\"i\"")
    val expected = Parsed.Success("h\"i", 6)
    assertResult(expected)(actual)
  }

  test("quotedField with single quote") {
    val actual = (Csv.quotedField ~ End).parse("\"h\"i\"")
    assert(actual.isInstanceOf[Parsed.Failure])
  }

  test("csv one field") {
    val actual = Csv.csv.parse("hi")
    val expected = Parsed.Success(Seq("hi"), 2)
    assertResult(expected)(actual)
  }

  test("csv one quoted field") {
    val actual = Csv.csv.parse("\"hi\"")
    val expected = Parsed.Success(Seq("hi"), 4)
    assertResult(expected)(actual)
  }

  test("csv1") {
    val actual = Csv.csv.parse("""hi""")
    val expected = Parsed.Success(Seq("hi"), 2)
    assertResult(expected)(actual)
  }

  test("csv2") {
    val actual = Csv.csv.parse("""hi,"there"""")
    val expected = Parsed.Success(Seq("hi", "there"), 10)
    assertResult(expected)(actual)
  }

  test("csv3") {
    val actual = Csv.csv.parse("""hi,"there",","""")
    val expected = Parsed.Success(Seq("hi", "there", ","), 14)
    assertResult(expected)(actual)
  }

  test("csv4") {
    val actual = Csv.csv.parse("""hi,"there",",","you""are"""")
    val expected = Parsed.Success(Seq("hi", "there", ",", "you\"are"), 25)
    assertResult(expected)(actual)
  }

  test("csv5") {
    val actual = Csv.csv.parse("""hi,"there",",","you""are",good""")
    val expected = Parsed.Success(Seq("hi", "there", ",", "you\"are", "good"), 30)
    assertResult(expected)(actual)
  }

  test("./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"") {
    val actual = Csv.csv.parse("./,\"CLOSE_WRITE,CLOSE\",\"\"\"hi\"")
    val expected = Parsed.Success(Seq("./", "CLOSE_WRITE,CLOSE", "\"hi"), 29)
    assertResult(expected)(actual)
  }

}
