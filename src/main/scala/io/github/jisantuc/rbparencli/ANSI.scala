package io.github.jisantuc.rbparencli

trait ColorCode {
  val colorText: String => String
  def next: ColorCode
}

object ColorCode {
  def apply(f: String => String, n: => ColorCode) = new ColorCode {
    val colorText = f
    def next = n
  }
}

object ANSI {
  private def interpolate(colorize: String, s: String): String = {
    s"${colorize}${s}\u001b[0m"
  }

  lazy val green = ColorCode(interpolate("\u001b[32m", _), yellow)
  lazy val yellow = ColorCode(interpolate("\u001b[33m", _), blue)
  lazy val blue = ColorCode(interpolate("\u001b[34m", _), magenta)
  lazy val magenta = ColorCode(interpolate("\u001b[35m", _), cyan)
  lazy val cyan: ColorCode = ColorCode(interpolate("\u001b[36m", _), green)
  lazy val red = ColorCode(interpolate("\u001b[31m", _), green)
}
