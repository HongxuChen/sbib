# SBIB

[![Build Status](https://travis-ci.org/HongxuChen/sbib.svg?branch=master)](https://travis-ci.org/HongxuChen/sbib)


## References
- [bibtool](https://github.com/ge-ne/bibtool), and its [CTAN page](https://www.ctan.org/tex-archive/biblio/bibtex/utils/bibtool/)
- [JabRef](http://www.jabref.org/)
- [BibTEX Entry and Field Types](https://www.andy-roberts.net/res/writing/latex/bibentries.pdf)
- [JBibTex](https://github.com/jbibtex/jbibtex)
- [Names in BibTEX and MlBibTEX](https://www.tug.org/TUGboat/tb27-2/tb87hufflen.pdf)
- [BibTex Wikipedia](https://en.wikipedia.org/wiki/BibTeX)
- [BIBTEXing](http://mirror.pregi.net/tex-archive/biblio/bibtex/base/btxdoc.pdf)
- [bibtex.org](http://www.bibtex.org/)
- [crossref.org](https://www.crossref.org/)

## Features

- [x] should base on RawDB rather than Entry
- [x] consistent with bibtool CLI and configurations
- [x] check possible duplicates
- [x] check fields of entries
- [ ] generation of key words (avoiding conflicts, crossref); keywords contains unicode?
- [ ] convert from `.aux` to `.bib` (as how `bibtool` does with `-x`)
- [ ] rewrite according to configurations
- [ ] update latex reference
- [ ] Web GUI <-- should be of top priority
- [ ] generation of entries
- [ ] dump to [bibjson](http://okfnlabs.org/bibjson/)
- [ ] perhaps [betterbib](https://github.com/nschloe/betterbib)
- [ ] https://github.com/vikin91/BibSpace
- [ ] dump to [COINS](http://ocoins.info/), as https://github.com/robintw/bib2coins
- [ ] more statistics like https://github.com/martinec/grav-plugin-bibtexify
- [ ] fix some issues for https://github.com/tcompa/sortbibtex#bibtex-style
- [ ] preview bibtex
- [ ] https://polsys.github.io/Ref
- [ ] rewrite should provide a dryrun like functionality

## development
- use shapeless/cats to deal with generic programming
- use Scala.js to deal with javascript
- use monocle do lens work
- [sbt-best-practice](https://zhuanlan.zhihu.com/p/22371242)

## Bugs
- scopt `help` seems not working correctly
- remove unnecessary brackets when analyzing

## Usage Scenario
- Windows users
- on a known web page
- command line
- within [Ammonite](http://www.lihaoyi.com/Ammonite/)
- development