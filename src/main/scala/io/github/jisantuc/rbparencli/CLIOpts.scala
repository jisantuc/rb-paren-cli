package io.github.jisantuc.rbparencli

import com.monovore.decline.Opts

object CLIOpts {
  def inFile = Opts.argument[String](metavar = "file").orNone
  def palette: Opts[Palette] = Opts
    .option[Palette](
      "palette",
      help = """
      | Comma-separated list of rgb(x,y,z) or hex colors (in 8 bit color space).
      | The last color in the list will be interpreted as the error color.
      | Example: rgb(0,255,255),rgb(255,0,255),rgb(255,255,0)
      |          000000,0000ff,00ff00,ff0000
      |""".trim.stripMargin
    )
    .withDefault(Palette.ansiDefault)
}
