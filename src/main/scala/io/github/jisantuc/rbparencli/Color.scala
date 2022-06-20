package io.github.jisantuc.rbparencli

import cats.data.NonEmptyList
import cats.data.Validated
import cats.data.ValidatedNel
import cats.kernel.Eq
import cats.parse.Numbers
import cats.parse.Parser
import cats.parse.Rfc5234._
import cats.syntax.eq._
import com.monovore.decline.Argument

case class Palette(
    colors: Ring[Color],
    error: Color
)

object Palette {
  def ansiDefault = Palette(
    Ring(ANSI.green, ANSI.yellow, ANSI.blue, ANSI.magenta, ANSI.cyan),
    ANSI.red
  )

  def ofColors(colors: NonEmptyList[Color], error: Color) = Palette(
    Ring.fromNonEmptyList(colors),
    error
  )

  implicit def eqPalette: Eq[Palette] = new Eq[Palette] {
    def eqv(x: Palette, y: Palette): Boolean =
      x.colors === y.colors && x.error === y.error
  }

  implicit val argPalette: Argument[Palette] = new Argument[Palette] {

    override def read(string: String): ValidatedNel[String, Palette] = {
      val comma = Parser.char(',')
      val rgbParser = (for {
        _ <- Parser.string("rgb(").void
        NonEmptyList(r, List(g, b)) <- Numbers.bigInt
          .repSep(
            3,
            3,
            comma
          )
          .map(_.map(_.toInt))
        _ <- Parser.string(")").void
      } yield RGB(r, g, b)).repSep(2, comma)
      val hexParser =
        hexdig
          .rep(2, 2)
          .map(_.toList.mkString(""))
          .map(Integer.parseInt(_, 16))
          .rep(3, 3)
          .map { case NonEmptyList(r, List(g, b)) =>
            RGB(r, g, b)
          }
          .repSep(2, comma)
      rgbParser
        .orElse(hexParser)
        .parseAll(string.replace("#", "").toLowerCase()) match {
        case Left(e) => Validated.invalidNel(s"$e")
        case Right(colors) => {
          val errorColor = colors.last
          val successColors =
            NonEmptyList(colors.head, colors.tail.dropRight(1))
          Validated.validNel(Palette.ofColors(successColors, errorColor))
        }
      }
    }

    override def defaultMetavar: String = "color,color,color,errorColor"

  }
}

sealed trait Color {
  val colorText: String => String
}

object Color {
  def apply(f: String => String) = new Color {
    val colorText = f
  }

  implicit val eqColor: Eq[Color] = new Eq[Color] {

    override def eqv(x: Color, y: Color): Boolean = {
      (x, y) match {
        case (x_ @ RGB(_, _, _), y_ @ (RGB(_, _, _))) =>
          x_ === y_
        case _ => false
      }
    }

  }
}

object ANSI {
  private def interpolate(colorize: String, s: String): String = {
    s"${colorize}${s}\u001b[0m"
  }

  lazy val green = Color(interpolate("\u001b[32m", _))
  lazy val yellow = Color(interpolate("\u001b[33m", _))
  lazy val blue = Color(interpolate("\u001b[34m", _))
  lazy val magenta = Color(interpolate("\u001b[35m", _))
  lazy val cyan: Color = Color(interpolate("\u001b[36m", _))
  lazy val red = Color(interpolate("\u001b[31m", _))
}

case class RGB(red: Int, green: Int, blue: Int) extends Color {
  override val colorText: String => String = { s =>
    s"\u001b[38;2;${red};${green};${blue}m${s}\u001b[0m"
  }
  def debug: String = s"rgb($red,$green,$blue)"
}

object RGB {

  implicit val eqRgb: Eq[RGB] = new Eq[RGB] {

    override def eqv(x: RGB, y: RGB): Boolean =
      x.red == y.red && x.green == y.green && x.blue == y.blue

  }
}
