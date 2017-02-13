package com.github.hongxuchen.sbib.view

import cats.data.{NonEmptyList => NEL}
import com.github.hongxuchen.sbib.core.EntryKey
import com.github.hongxuchen.sbib.utils.Utility._
import monocle.macros.GenLens
import shapeless.LabelledGeneric
import com.github.hongxuchen.sbib.errors.Handling.{MonthKErrorExcept, fieldRequire}
import com.github.hongxuchen.sbib.core.FT._

// TODO perhaps make k-v to be Map[String, Value]

case class Name(s: String) extends Ordered[Name] {
  fieldRequire(s.nonEmpty && s.head.isUpper)("name", s"|${s}| unsatisfied")

  def toAbbrev: String = if (isAbbrev) s else s"${s.head}."

  def isAbbrev: Boolean = s.endsWith(".")

  override def compare(that: Name): Int = {
    if (this.s > that.s) 1 else if (this.s < that.s) -1 else 0
  }
}

case class PersonName(firstName: Name, givenList: List[Name]) extends Ordered[PersonName] {
  fieldRequire(!firstName.isAbbrev)("personName", s"familyName: ${firstName}")

  override def compare(that: PersonName): Int = {
    if (this.firstName > that.firstName) 1
    else {
      val thisLen = this.givenList.length
      val thatLen = that.givenList.length
      ???
    }
  }
}

// TODO fix {} issues
// http://tex.stackexchange.com/questions/204697/how-to-correctly-typeset-an-authors-two-word-last-name-in-bibtex
object PersonName {
  val firstName = GenLens[PersonName](_.firstName)
  val lastName = GenLens[PersonName](_.givenList)

  def apply(ss: String): PersonName = {
    // TODO this is ad-hoc
    val s = ss.replaceAll("[{}\"]", "")
    // for with commas, prior is surname
    if (s.contains(", ")) {
      val names = s.split(", ").toList
      fieldRequire(names.length == 2)("personName", s"${names.mkString("[", ", ", "]")}")
      val family = Name(names.head)
      val givenList = names(1).split(" ").toList.map(Name)
      PersonName(family, givenList)
    } else {
      val names = s.split(" ").toList
      fieldRequire(names.length >= 2)("personName", s"${names.mkString("[", ", ", "]")}")
      val family = Name(names.last)
      val givenList = names.init.map(Name)
      PersonName(family, givenList)
    }
  }
}

sealed trait Field {
  def label: String
}


object Field {
  type AuthorOrEditor = Either[Author, Editor]
  type VolumeOrNumber = Either[Volume, Number]
  type ChapterOrPages = Either[Chapter, Pages]
}

case class Address(s: String) extends Field {
  val label = F_address
}

case class Annote(s: String) extends Field {
  val label = F_annote
}


// TODO distinguish editor and author
trait PersonField extends Field with Ordered[PersonField] {
  val personNameList: NEL[PersonName]

  def fn: String

  override def compare(that: PersonField) = ???

}

case class Author(personNameList: NEL[PersonName]) extends PersonField {
  val label = F_author

  def fn: String = personNameList.head.firstName.s

}

object Author {
  val authorGen = LabelledGeneric[Author]

  def apply(v: String): Author = {
    val authors = v.normalized.split("\\s+and\\s+").map(PersonName(_)).toList
    val authorList = NEL(authors.head, authors.tail)
    Author(authorList)
  }
}

case class BookTitle(s: String) extends Field {
  val label = F_booktitle
}

case class Chapter(s: String) extends Field {
  val label = F_chapter
  fieldRequire(s.forall(_.isDigit))(label, s"${s} should be all digits")
}

case class CrossRef(entryKey: EntryKey)(var entryVal: Entry) extends Field {
  val label = F_crossref

  def isResolved: Boolean = false

  def setEntry(entry: Entry): Unit = entryVal = entry
}

case class Edition(s: String) extends Field {
  val label = F_edition
  fieldRequire(s.head.isUpper || s.head.isDigit)(label, s"${s} should be captalized/digits")
}

case class Editor(personNameList: NEL[PersonName]) extends PersonField {
  val label = F_editor

  def fn = personNameList.head.firstName.s
}

object Editor {
  val editorGen = LabelledGeneric[Editor]

  import Author._

  def apply(v: String): Editor = editorGen.from(authorGen.to(Author(v)))
}

case class HowPublished(s: String) extends Field {
  val label = F_howpublished
  fieldRequire(s.head.isUpper)(label, s"${s} should be captalized")
}

case class Institution(s: String) extends Field {
  val label = F_institution
}

case class Journal(s: String) extends Field {
  val label = F_journal
}

case class Key(s: String) extends Field {
  val label = F_key
}

object MonthEnum extends Enumeration {
  type Ty = Value
  val Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec = Value
}

case class Month(monthEnum: MonthEnum.Ty) extends Field {
  val label = F_month
}

object Month {

  def apply(v: String): Month = {
    val rawString = v
    val s = rawString.substring(0, 3).toLowerCase.capitalize
    fieldRequire(MonthEnum.values.exists(_.toString == s))("month", rawString)
    try {
      val monthEnum = MonthEnum.withName(s)
      Month(monthEnum)
    } catch {
      case e: NoSuchElementException => {
        throw new MonthKErrorExcept(s)
      }
    }
  }
}

case class Note(s: String) extends Field {
  val label = F_note
  val ss = s.normalized.capitalize
  fieldRequire(ss.head.isUpper)(label, s"${ss} should be captalized")
}

case class Number(s: String) extends Field {
  val label = F_number
  fieldRequire(s.head.isUpper || s.head.isDigit)(label, s"${s} should be captalized/digits")
}


case class Organization(s: String) extends Field {
  val label = F_organization
}

/**
  * 42–111 or 7,41,73–97 or 43+ (the ‘+’ in this last example indicates pages following that don’t form a simple range)
  */
case class Pages(s: String) extends Field {
  val label = F_pages
}

case class Publisher(s: String) extends Field {
  val label = F_publisher
}

case class School(s: String) extends Field {
  val label = F_school
}

case class Series(s: String) extends Field {
  val label = F_series
}

case class Title(s: String) extends Field {
  val label = F_title
}

case class BType(s: String) extends Field {
  val label = F_type
}

case class Volume(s: String) extends Field {
  val label = F_volume
}

case class Year(s: String) extends Field {
  // TODO check: year should be within a range
  // ref: https://github.com/jlibovicky/prettybib
  val label = F_year
  val last4NonPunctuation: String = s.filter(_.isDigit)
  fieldRequire(last4NonPunctuation.length >= 4)(label, s"${s}:len(${last4NonPunctuation}) should be >= 4")
}

case class Doi(s: String) extends Field {
  val label = F_doi
}

case class EPrint(s: String) extends Field {
  val label = F_eprint
}

case class Url(s: String) extends Field {
  val label = F_url
}