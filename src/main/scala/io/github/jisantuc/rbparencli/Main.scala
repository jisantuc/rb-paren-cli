package io.github.jisantuc.rbparencli

import cats.effect.ExitCode
import cats.effect.IO
import cats.syntax.apply._
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import fs2.io.file.Files
import fs2.io.file.Path

object Main
    extends CommandIOApp(
      "rainbow-parens",
      "Rainbow-ize parentheses, braces, and brackets in streaming input",
      true,
      "0.0.1"
    ) {

  private var openCount: Int = 0

  private def colorize(palette: Palette)(c: Char): String = {
    val openChars = Set('[', '(', '{')
    val closeChars = Set(']', ')', '}')
    c match {
      case ch if openChars.contains(ch) =>
        openCount += 1
        val nextColor = palette.colors.next
        nextColor.colorText(s"$ch")
      case ch if closeChars.contains(ch) =>
        if (openCount == 0) {
          palette.error.colorText(s"$ch")
        } else {
          openCount -= 1
          val nextColor = palette.colors.previous
          nextColor.colorText(s"$ch")
        }
      case c => s"$c"
    }
  }

  override def main: Opts[IO[ExitCode]] =
    (CLIOpts.inFile, CLIOpts.palette).mapN { case (inFileO, palette) =>
      val MaxSafeLong = 9007199254740991L
      val inputCharStream = inFileO match {
        case Some(inFilePath) =>
          Files[IO].readRange(Path(inFilePath), 64 * 1024, 0, MaxSafeLong)
        case None =>
          fs2.io.stdin[IO](16 * 16)
      }

      val colorizer = (b: Byte) => colorize(palette)(b.toChar)

      inputCharStream
        .map(colorizer)
        .flatMap(s => fs2.Stream.emits(s.getBytes()).covary[IO])
        .through(fs2.io.stdout[IO])
        .compile
        .drain
        .as(ExitCode.Success)
    }

}
