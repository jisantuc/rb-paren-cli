package io.github.jisantuc.rbparencli

import cats.data.NonEmptyList

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
}

trait Color {
  val colorText: String => String
}

object Color {
  def apply(f: String => String) = new Color {
    val colorText = f
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

class RGB(red: Int, green: Int, blue: Int) extends Color {
  override val colorText: String => String = { s =>
    s"\x1b[38;2;${red};${green};${blue}m${s}\u001b[0m"
  }
}

object RGB {
  def apply(red: Int, green: Int, blue: Int): Color = Color(
    RGB(red, green, blue).colorText
  )
}
