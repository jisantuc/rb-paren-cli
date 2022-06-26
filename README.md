# `rainbow-parens`

The CLI, [not](https://github.com/Fanael/rainbow-delimiters) [the](https://github.com/luochen1990/rainbow) [editor](https://www.rstudio.com/blog/rstudio-1-4-preview-rainbow-parentheses/)
[extension](https://marketplace.visualstudio.com/items?itemName=2gua.rainbow-brackets).

[![CI](https://github.com/jisantuc/rb-paren-cli/actions/workflows/ci.yml/badge.svg)](https://github.com/jisantuc/rb-paren-cli/actions/workflows/ci.yml)

`rainbow-parens` is a small console utility for adding rainbow
parentheses to unix streaming stdin.

If you're not familiar with rainbow parentheses, they're an editor
feature where each pair of parentheses, braces, and brackets is rendered
a distinct color, so you can find the open and close more easily when
looking at the screen. It's great! Sometimes you want to be able to
add rainbow parentheses to other things though, for instance, if you're
writing tests and need to dump a large complex JSON object, or you're
staring at logs from a system with a complex log format. This little
CLI lets you add rainbow brackets to any file or streaming input.

[![asciicast](https://asciinema.org/a/W4h5iyicNf2At4SEGlkqHxgJM.svg)](https://asciinema.org/a/W4h5iyicNf2At4SEGlkqHxgJM)

## Installation

A Linux-compatible binary and NodeJS script will be published for each 
release. The NodeJS script should work for everyone and is smaller than 
the published binary by a considerable amount but also requires NodeJS.

### Linux Binary

If you're on a Linux system, you can download the binary and put it 
somewhere on your path and you'll be all set, e.g.:

```bash
# copy the actual URL from the latest release
$ wget https://github.com/jisantuc/rb-paren-cli/... 
$ cp rainbow-parens /usr/local/bin/
```

### NodeJS script

If you're not on a Linux system or your Linux system is incompatible with
the compiled binary, you can use the NodeJS script. To do so, download 
the script somewhere sensible then create an alias that invokes it with
NodeJS. For example:

```bash
# copy the actual URL from the latest release
$ wget \
    -o $HOME/.local/bin/rainbow-parens.js \
    https://github.com/jisantuc/rb-paren-cli/...
# or .bash_profile, or .bashrc, or wherever it is you configure aliases
# for your shell sessions
$ echo 'alias rainbow-parens="node $HOME/.local/bin/rainbow-parens.js"' >> ~/.zshrc
```

Then you can use `rainbow-parens` normally.

## Color configuration

The default rainbow uses ANSI red for un-matched closing characters and
ANSI colors for the rainbow. But if you want, you can have so many
more colors!

The CLI accepts a `palette` argument that will let you specify the color
rotation in two different ways -- you can use comma-separated hex colors
(with or without leading `#` symbols) or comma-separated
`rgb(byte,byte,byte)` values. The last value in the list will be treated
as the error color, so you _must_ specify at least two values if you want
to configure a palette.

You can see example palette configurations in the CLI help:

```
$ rainbow-parens --help
Usage: rb-paren-cli [--palette <color,color,color,errorColor>] [<file>]

...
    --palette <color,color,color,errorColor>
         Comma-separated list of rgb(x,y,z) or hex colors (in 8 bit color space).
         The last color in the list will be interpreted as the error color.
         Example: rgb(0,255,255),rgb(255,0,255),rgb(255,255,0)
                  000000,0000ff,00ff00,ff0000
```

The two examples aren't very pretty, but they get the point across. If 
you want to generate some palettes, you can check out
[`coolors`](https://coolors.co/generate) and mess with tints to get more
colors. For instance, using colors from [this random generation](https://coolors.co/fb5012-01fdf6-cbbaed-e9df00-03fcba):

[![asciicast](https://asciinema.org/a/HEl8XrCxQelYhcub45YabpkSq.svg)](https://asciinema.org/a/HEl8XrCxQelYhcub45YabpkSq)