package io.github.jisantuc.rbparencli

import cats.data.NonEmptyList
import cats.data.Validated
import com.monovore.decline.Argument
import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop._

class ColorSpec extends ScalaCheckSuite {
  val byteGen = Gen.choose(0, 255)
  val rgbGen = for {
    r <- byteGen
    g <- byteGen
    b <- byteGen
  } yield (r, g, b)

  private def leftPad(s: String, n: Int, c: Char): String = if (s.size >= n) {
    s
  } else {
    val difference = n - s.size
    s"$c" * difference ++ s
  }

  private def toColorComponent(n: Int): String =
    leftPad(n.toHexString.toLowerCase(), 2, '0')

  private def rgbTupleToHexString(
      tup: (Int, Int, Int),
      prefix: String
  ): String = {
    val (r, g, b) = tup
    s"$prefix${toColorComponent(r)}${toColorComponent(g)}${toColorComponent(b)}"
  }

  private def makePalette(
      successTuple: (Int, Int, Int),
      errorTuple: (Int, Int, Int)
  ): Palette = {
    val (sr, sg, sb) = successTuple
    val (er, eg, eb) = errorTuple
    Palette.ofColors(NonEmptyList.of(RGB(sr, sg, sb)), RGB(er, eg, eb))
  }

  def debugColor(color: Color) = color match {
    case c: RGB => c.toString()
    case c      => c.toString
  }

  property(
    "Palette parsing succeds with rgb(...) color specification"
  ) {
    forAll(rgbGen, rgbGen) { case (success @ (r, g, b), error @ (er, eg, eb)) =>
      Argument[Palette].read(
        s"rgb($r,$g,$b),rgb($er,$eg,$eb)"
      ) === Validated.validNel(
        makePalette(success, error)
      )
    }
  }

  hexTest("#")
  hexTest("")

  private def hexTest(prefix: String) = property(
    s"Palette parsing succeds with $prefix-preceded hex string color specification"
  ) {
    forAll(rgbGen, rgbGen) { case (successColor, errColor) =>
      val successColorString = rgbTupleToHexString(successColor, prefix)
      val errColorString = rgbTupleToHexString(errColor, prefix)

      Argument[Palette].read(
        s"$successColorString,$errColorString"
      ) ===
        Validated.validNel(
          makePalette(successColor, errColor)
        )
    }
  }

}
