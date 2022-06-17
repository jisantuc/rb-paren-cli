package io.github.jisantuc.rbparencli

import com.monovore.decline.Opts

object CLIOpts {
  def inFile = Opts.argument[String](metavar = "file").orNone
}
