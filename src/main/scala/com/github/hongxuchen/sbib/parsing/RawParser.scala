package com.github.hongxuchen.sbib.parsing

import cats.data.Validated.{Invalid, Valid}
import com.github.hongxuchen.sbib.core._
import com.github.hongxuchen.sbib.errors.RawParseError

import scala.collection.immutable.ListMap

object RawParser {

  // TODO @, = are also special string

  import fastparse.all._

  private[parsing] val entryNameP = P {
    val ss = ET.values.map(_.entryName)
    StringInIgnoreCase(ss: _*)
  }.opaque("[entry]")

  val C = ","
  val LB = "{"
  val RB = "}"
  val Q = "\""
  val SPECIAL = s"${Q}${LB}${RB}"

  val alpha = CharIn('a' to 'z', 'A' to 'Z')

  private[parsing] val fieldNameP = P {
    alpha.rep(1) ~ ("_" ~ alpha.rep(1)).rep
  }.opaque("[field]")

  // FIXME currently it will eat ALL "ws", perhaps unexpected
  private val StrChars = P(CharsWhile(!s"\\${SPECIAL}".contains(_: Char)))
  private val StrCharsNoComma = P(CharsWhile(!s"\\${SPECIAL}${C}".contains(_: Char)))

  // TODO not sure
  private val UnicodeEscape = P {
    val hexDigit = P(CharIn('0' to '9', 'a' to 'f', 'A' to 'F'))
    "u" ~ hexDigit.rep(exactly = 4)
  }
  private val MiscEscape = {
    // \a, \b, ...
    "\\" ~ CharIn('a' to 'z', s"${SPECIAL}'&\\")
  }
  //  val UrlEscape = P(s"\\url${LB}" ~ StrChars ~ s"${RB}")
  //  val RegularEscape = "\\" ~ CharIn(s"${Q}/\\bfnrt")
  private val Escape = P(MiscEscape | UnicodeEscape)

  // whether there is a pair of {}/"" doesn't matter
  // when there is, region can have special character like ",", while raw doesn't
  // "many" can be several "one"
  /**
    * {{abc}}
    * {abc,{def},gh}
    * abcdef{g"i"h}
    * abcdef,gh <- reject
    */

  private val pAtomic: P[Unit] = (Escape | StrChars).rep(1)
  private val uAtomic: P[Unit] = (Escape | StrCharsNoComma).rep(1)

  private val pOne: P[Unit] = P(pAtomic | (LB ~ pOne.rep(1) ~ RB) | (Q ~ pOne.rep(1) ~ Q))

  private val one: P[Unit] = P(uAtomic | (LB ~ pOne.rep(1) ~ RB) | (Q ~ pOne.rep(1) ~ Q))

  val StringBulk: P[Unit] = P(one.rep(1))

  private val ws = P(CharIn(" \t\n"))

  // whitespaces are optional
  private val rawSep = P(ws.rep)

  // the separator between entries
  private val entrySep = P("\n".rep(1))

  // the separator inside entries, mostly for fields
  // no cuts inside
  private val fieldSep = P(C ~ ws.rep)

  val entryKeyP = StringBulk
  val fieldContent = StringBulk

  val fieldItemP = P {
    fieldNameP.! ~/ rawSep ~/ "=" ~/ rawSep ~/ fieldContent.!
  }

  val fieldItemsP = P {
    fieldItemP.rep(min = 1, sep = fieldSep) ~/ C.? ~/ rawSep
  }

  val entryItem = P {
    "@" ~/ entryNameP.! ~/ LB ~/ entryKeyP.! ~/ fieldSep ~/ fieldItemsP ~/ RB ~/ CharIn(" \t").rep
  }.map { case (name, key, field) => {
    val entryKey = EntryKey(key)
    entryKey -> RawEntry(entryKey, ET.withNameInsensitive(name), ListMap(field: _*))
  }
  }

  val entryItemsP = P(entryItem.rep(min = 1, sep = entrySep))

  val bibP = P(rawSep ~ entryItemsP ~ rawSep ~ End).map(RawDB(_))

  def parseDB(input: String): ValidationRawDB = {
    val result = bibP.parse(input)
    result match {
      case Parsed.Success(a, _) => Valid(a)
      // TODO make it precise
      case Parsed.Failure(expected, index, extra) => Invalid {
        RawParseError(expected.toString, index, extra.toString)
      }
    }
  }

}