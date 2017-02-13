package com.github.hongxuchen.sbib.writer

import cats.data.{NonEmptyList => NEL}
import com.github.hongxuchen.sbib.core.EntryKey
import com.github.hongxuchen.sbib.view.{Entry, Field, PersonField, PersonName}
import com.github.hongxuchen.sbib.view._

class Show(changeEntryKey: Boolean = false) {

  // TODO use higher kinded with cats

  def show(entry: Entry): String = {
    val entryKey = {
      if (changeEntryKey) {
        val generator = new EntryKeyGenerator(entry)
        generator.genKey()
      } else {
        entry.entryKey
      }
    }
    s"""
       |@${entry.label}{${show(entryKey)},
       |${showFields(entry)},
       |}""".stripMargin
  }

  private def show(entryKey: EntryKey): String = entryKey.s

  private def showFields(entry: Entry): String = {
    val fields: List[Field] = entry match {
      case Article(author, title, journal, year, volume, number, pages,
      month, note, _) => {
        List(author, title, journal, year) ++
          List(volume, number, pages, month, note).flatten
      }
      case Book(authorOrEditor, title, publisher, year,
      volumeOrNumber, series, address, edition, month, note, _) => {
        List(flattenEither(authorOrEditor), title, publisher, year) ++
          List(flattenEitherOpt(volumeOrNumber), series, address, edition, month, note).flatten
      }
      case Booklet(title, author, howPublished, address, month, year, note, _) => {
        List(title) ++
          List(author, howPublished, address, month, year, note).flatten
      }
      case Conference(author, title, bookTitle, year,
      editor, volumeOrNumber, series, pages, address, month,
      organization, publisher, note, _) => {
        List(author, title, bookTitle, year) ++
          List(editor, flattenEitherOpt(volumeOrNumber), series, pages, address, month,
            organization, publisher, note).flatten
      }
      case InBook(authorOrEditor, title, chapterOrPagesList, publisher, year,
      volumeOrNumber, series, bType, address, edition,
      month, note, _) => {
        List(flattenEither(authorOrEditor), title, publisher, year) ++
          flattenEitherNEL(chapterOrPagesList).toList ++
          List(flattenEitherOpt(volumeOrNumber), series, bType, address, edition, month, note).flatten
      }
      case InCollection(author, bookTitle, publisher, year,
      editor, volumeOrNumber, series, bType, chapter, pages,
      address, edition, month, note, _) => {
        List(author, bookTitle, publisher, year) ++
          List(editor, flattenEitherOpt(volumeOrNumber), series, bType, chapter,
            pages, address, edition, month, note).flatten
      }
      case InProceedings(author, title, bookTitle, year,
      editor, volumeOrNumber, series, pages, address, month,
      organization, publisher, note, _) => {
        List(author, title, bookTitle, year) ++
          List(editor, flattenEitherOpt(volumeOrNumber), series, pages, address,
            month, organization, publisher, note).flatten
      }
      case Manual(title, author, organization, address, edition, month, year, note, _) => {
        List(title) ++ List(author, organization, address, edition, month, year, note).flatten
      }
      case MastersThesis(author, title, school, year,
      bType, address, month, note, _) => {
        List(author, title, school, year) ++ List(bType, address, month, note).flatten
      }
      case Misc(author, title, howPublished, month, year, note, _) => {
        List(author, title, howPublished, month, year, note).flatten
      }
      case PhdThesis(author, title, school, year,
      bType, address, month, note, _) => {
        List(author, title, school, year) ++ List(bType, address, month, note).flatten
      }
      case Proceedings(title, year,
      editor, volumeOrNumber, series, address,
      month, organization, publisher, note, _) => {
        List(title, year) ++ List(editor, flattenEitherOpt(volumeOrNumber), series, address,
          month, organization, publisher, note).flatten
      }
      case TechReport(author, title, institution, year,
      bType, address, month, note, _) => {
        List(author, title, institution, year) ++ List(bType, address, month, note).flatten
      }
      case UnPublished(author, title, note,
      month, year, _) => {
        List(author, title, note) ++ List(month, year).flatten
      }
    }
    show(fields)
  }

  def flattenEither[A <: Field, B <: Field](ab: Either[A, B]):
  Field = ab match {
    case Left(a) => a
    case Right(b) => b
  }

  def flattenEitherOpt[A <: Field, B <: Field](ab: Option[Either[A, B]]):
  Option[Field] = ab map {
    case Left(a) => a
    case Right(b) => b
  }

  def flattenEitherNEL[A <: Field, B <: Field](ab: NEL[Either[A, B]]):
  NEL[Field] = ab map {
    case Left(a) => a
    case Right(b) => b
  }

  private def show(fields: List[Field]): String = {
    fields.map { f =>
      f"${f.label}%-15s = {${show(f)}%s}"
    }.mkString(",\n")
  }

  private def show(field: Field): String = field match {
    case Address(s) => s
    case Annote(s) => s
    case BookTitle(s) => s
    case Chapter(s) => s
    case CrossRef(entryKey) => entryKey.s
    case Edition(s) => s
    case HowPublished(s) => s
    case Institution(s) => s
    case Journal(s) => s
    case Key(s) => s
    case Month(monthEnum) => monthEnum.toString
    case Note(s) => s
    case Number(s) => s
    case Organization(s) => s
    case Pages(s) => s
    case Publisher(s) => s
    case School(s) => s
    case Series(s) => s
    case Title(s) => s
    case BType(s) => s
    case Volume(s) => s
    case Year(s) => s
    case Doi(s) => s
    case EPrint(s) => s
    case Url(s) => s
    case p:PersonField => show(p.personNameList)

  }

  def show(personNameList: NEL[PersonName]): String = {
    val persons = personNameList.map { personName =>
      personName.givenList.map(_.toAbbrev).mkString(" ") + " " + personName.firstName.s
    }
    persons.toList.mkString(" and ")
  }

}
