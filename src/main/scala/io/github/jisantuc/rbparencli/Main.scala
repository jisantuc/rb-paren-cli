package io.github.jisantuc.rbparencli

import cats.effect.ExitCode
import cats.effect.IO
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import fs2.io.file.Files
import fs2.io.file.Path

object Main
    extends CommandIOApp(
      "rb-paren-cli",
      "Rainbow-ize parentheses, braces, and brackets in streaming input",
      true,
      "0.0.1"
    ) {

  var colorStack = List.empty[Color]

  val palette = Palette.ansiDefault

  private def colorize(c: Char): String = {
    val openChars = Set('[', '(', '{')
    val closeChars = Set(']', ')', '}')
    c match {
      case ch if openChars.contains(ch) =>
        val nextColor = palette.colors.next
        colorStack = colorStack :+ nextColor
        nextColor.colorText(s"$ch")
      case ch if closeChars.contains(ch) =>
        if (colorStack.isEmpty) {
          palette.error.colorText(s"$ch")
        } else {
          val nextColor = colorStack.last
          colorStack = colorStack.dropRight(1)
          nextColor.colorText(s"$ch")
        }
      case c => s"$c"
    }
  }

  override def main: Opts[IO[ExitCode]] = CLIOpts.inFile.map { inFileO =>
    val MaxSafeLong = 9007199254740991L
    val inputCharStream = inFileO match {
      case Some(inFilePath) =>
        Files[IO].readRange(Path(inFilePath), 64 * 1024, 0, MaxSafeLong)
      case None =>
        fs2.io.stdin[IO](16 * 16)
    }

    inputCharStream
      .map({ b => colorize(b.toChar) })
      .flatMap(s => fs2.Stream.emits(s.getBytes()).covary[IO])
      .through(fs2.io.stdout[IO])
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
