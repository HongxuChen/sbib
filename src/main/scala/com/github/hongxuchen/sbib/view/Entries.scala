package com.github.hongxuchen.sbib.view

import cats.data.{NonEmptyList => NEL}
import com.github.hongxuchen.sbib.core.{CFEntry, ET, EntryKey}
import com.github.hongxuchen.sbib.utils.Utility._
import com.github.hongxuchen.sbib.view.Field.{AuthorOrEditor, ChapterOrPages, VolumeOrNumber}
import shapeless.LabelledGeneric

sealed trait Entry {
  def entryKey: EntryKey

  def label: ET

}

/**
  * Required fields: author, title, journal, year.
  * Optional fields: volume, number, pages, month, note.
  */
case class Article(author: Author, title: Title, journal: Journal, year: Year,
                   volume: Option[Volume], number: Option[Number], pages: Option[Pages],
                   month: Option[Month], note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.Article
}

import com.github.hongxuchen.sbib.core.FT._

object Article {
  def apply(entry: CFEntry): Article = {
    val article = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val title = Title(fields(F_title))
      val journal = Journal(fields(F_journal))
      val year = Year(fields(F_year))
      val volume = fields.get(F_volume).map(Volume)
      val number = fields.get(F_number).map(Number)
      val pages = fields.get(F_pages).map(Pages)
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      Article(author, title, journal, year, volume, number, pages, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    article(entryKey)
  }
}

/**
  * Required fields: author or editor, title, publisher, year.
  * Optional fields: volume or number, series, address, edition, month, note
  */
case class Book(authorOrEditor: AuthorOrEditor, title: Title, publisher: Publisher, year: Year,
                volumeOrNumber: Option[VolumeOrNumber], series: Option[Series], address: Option[Address],
                edition: Option[Edition], month: Option[Month], note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.Book
}

object Book {
  def apply(entry: CFEntry): Book = {
    val book = {
      val fields = entry.fields
      val authorOrEditor = {
        val author = fields.get(F_author).map(Author(_))
        val editor = fields.get(F_editor).map(Editor(_))
        (author, editor) match {
          case (Some(a), Some(e)) => {
            reportWarn(s"${a}:${e}")
            Left(a)
          }
          case (Some(a), None) => Left(a)
          case (None, Some(e)) => Right(e)
          case (None, None) => ???
        }
      }
      val title = Title(fields(F_title))
      val publisher = Publisher(fields(F_publisher))
      val year = Year(fields(F_year))
      val volumeOrNumber = {
        val volume = fields.get(F_volume).map(Volume)
        val number = fields.get(F_number).map(Number(_))
        (volume, number) match {
          case (Some(v), Some(n)) => {
            reportWarn(s"${v}:${n}")
            Some(Left(v))
          }
          case (Some(v), None) => Some(Left(v))
          case (None, Some(n)) => Some(Right(n))
          case (None, None) => None
        }
      }
      val series = fields.get(F_series).map(Series(_))
      val address = fields.get(F_address).map(Address(_))
      val edition = fields.get(F_edition).map(Edition(_))
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      Book(authorOrEditor, title, publisher, year, volumeOrNumber, series, address, edition, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    book(entryKey)
  }
}

/**
  * Required field: title.
  * Optional fields: author, howpublished, address, month, year. note.
  */
case class Booklet(title: Title,
                   author: Option[Author], howPublished: Option[HowPublished], address: Option[Address],
                   month: Option[Month], year: Option[Year], note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.Booklet
}

object Booklet {
  def apply(entry: CFEntry): Booklet = {
    val booklet = {
      val fields = entry.fields
      val title = Title(fields(F_title))
      val author = fields.get(F_author).map(Author(_))
      val howPublished = fields.get(F_publisher).map(HowPublished(_))
      val address = fields.get(F_address).map(Address(_))
      val month = fields.get(F_month).map(Month(_))
      val year = fields.get(F_year).map(Year(_))
      val note = fields.get(F_note).map(Note(_))
      Booklet(title, author, howPublished, address, month, year, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    booklet(entryKey)
  }
}


// same as inproceedings
case class Conference(author: Author, title: Title, bookTitle: BookTitle, year: Year,
                      editor: Option[Editor], volumeOrNumber: Option[VolumeOrNumber], series: Option[Series],
                      pages: Option[Pages], address: Option[Address], month: Option[Month],
                      organization: Option[Organization], publisher: Option[Publisher],
                      note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.Conference
}

object Conference {
  val conferenceGen = LabelledGeneric[Conference]

  import InProceedings.inProceedingsGen

  def apply(entry: CFEntry): Conference = conferenceGen.from(inProceedingsGen.to(InProceedings(entry)))
}


/** Required: author or editor, title, chapter and/or pages, publisher, year
  * Optional: volume or number, series, type, address, edition, month, note
  */
case class InBook(authorOrEditor: AuthorOrEditor, title: Title, chapterOrPagesList: NEL[ChapterOrPages], publisher: Publisher, year: Year,
                  volumeOrNumber: Option[VolumeOrNumber], series: Option[Series], bType: Option[BType],
                  address: Option[Address], edition: Option[Edition], month: Option[Month],
                  note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.InBook
}

object InBook {
  def apply(entry: CFEntry): InBook = {
    val inBook = {
      val fields = entry.fields
      // TODO springer may be wrong in including both
      val authorOrEditor = {
        val author = fields.get(F_author).map(Author(_))
        val editor = fields.get(F_editor).map(Editor(_))
        (author, editor) match {
          case (Some(a), Some(e)) => {
            reportWarn(s"${a} vs ${editor}")
            Left(a)
          }
          case (Some(a), None) => Left(a)
          case (None, Some(e)) => Right(e)
          case (None, None) => ???
        }
      }
      val title = Title(fields(F_title))
      val chapterOrPagesList = {
        val chapter = fields.get(F_chapter).map(Chapter(_))
        val pages = fields.get(F_pages).map(Pages(_))
        (chapter, pages) match {
          case (Some(c), Some(p)) => NEL.of(Left(c), Right(p))
          case (Some(c), None) => NEL.of(Left(c))
          case (None, Some(p)) => NEL.of(Right(p))
          case (None, None) => ???
        }
      }
      val publisher = Publisher(fields(F_publisher))
      val year = Year(fields(F_year))
      val volumeOrNumber = {
        val volume = fields.get(F_volume).map(Volume(_))
        val number = fields.get(F_number).map(Number(_))
        (volume, number) match {
          case (Some(v), Some(n)) => ???
          case (Some(v), None) => Some(Left(v))
          case (None, Some(n)) => Some(Right(n))
          case (None, None) => None
        }
      }
      val series = fields.get(F_series).map(Series(_))
      val bType = fields.get(F_type).map(BType(_))
      val address = fields.get(F_address).map(Address(_))
      val edition = fields.get(F_edition).map(Edition(_))
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      InBook(authorOrEditor, title, chapterOrPagesList, publisher, year, volumeOrNumber, series, bType, address, edition, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    inBook(entryKey)
  }
}

/**
  * Required: author, title, booktitle, publisher, year.
  * Optional: editor, volume or number, series, type, chapter, pages, address, edition, month, note.
  */
case class InCollection(author: Author, bookTitle: BookTitle, publisher: Publisher, year: Year,
                        editor: Option[Editor], volumeOrNumber: Option[VolumeOrNumber],
                        series: Option[Series], bType: Option[BType],
                        chapter: Option[Chapter], pages: Option[Pages], address: Option[Address],
                        edition: Option[Edition], month: Option[Month],
                        note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.InCollection
}

object InCollection {
  def apply(entry: CFEntry): InCollection = {
    val inCollection = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val bookTitle = BookTitle(fields(F_booktitle))
      val publisher = Publisher(fields(F_publisher))
      val year = Year(fields(F_year))
      val editor = fields.get(F_editor).map(Editor(_))
      val volumeOrNumber = {
        val volume = fields.get(F_volume).map(Volume(_))
        val number = fields.get(F_number).map(Number(_))
        (volume, number) match {
          case (Some(v), Some(n)) => {
            reportWarn(s"${v}:${n}")
            Some(Left(v))
          }
          case (Some(v), None) => Some(Left(v))
          case (None, Some(n)) => Some(Right(n))
          case (None, None) => None
        }
      }
      val series = fields.get(F_series).map(Series(_))
      val bType = fields.get(F_type).map(BType(_))
      val chapter = fields.get(F_chapter).map(Chapter(_))
      val pages = fields.get(F_pages).map(Pages(_))
      val address = fields.get(F_address).map(Address(_))
      val edition = fields.get(F_edition).map(Edition(_))
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      InCollection(author, bookTitle, publisher, year,
        editor, volumeOrNumber, series, bType, chapter, pages, address, edition, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    inCollection(entryKey)
  }
}

/**
  * Required fields: author, title, booktitle, year.
  * Optional fields: editor, volume or number, series, pages, address, month, organization, publisher, note.
  */
case class InProceedings(author: Author, title: Title, bookTitle: BookTitle, year: Year,
                         editor: Option[Editor], volumeOrNumber: Option[VolumeOrNumber], series: Option[Series],
                         pages: Option[Pages], address: Option[Address], month: Option[Month],
                         organization: Option[Organization], publisher: Option[Publisher],
                         note: Option[Note], entryKey: EntryKey) extends Entry {
  val label = ET.InProceedings
}

object InProceedings {
  val inProceedingsGen = LabelledGeneric[InProceedings]

  def apply(entry: CFEntry): InProceedings = {
    val inProceedings = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val title = Title(fields(F_title))
      val bookTitle = BookTitle(fields(F_booktitle))
      val year = Year(fields(F_year))
      val editor = fields.get(F_editor).map(Editor(_))
      val volumeOrNumber = {
        val volume = fields.get(F_volume).map(Volume(_))
        val number = fields.get(F_number).map(Number(_))
        (volume, number) match {
          case (Some(v), Some(n)) => {
            reportWarn(s"${v} vs ${n}")
            Some(Left(v))
          }
          case (Some(v), None) => Some(Left(v))
          case (None, Some(n)) => Some(Right(n))
          case (None, None) => None
        }
      }
      val series = fields.get(F_series).map(Series(_))
      val pages = fields.get(F_pages).map(Pages(_))
      val address = fields.get(F_address).map(Address(_))
      val month = fields.get(F_month).map(Month(_))
      val organization = fields.get(F_organization).map(Organization(_))
      val publisher = fields.get(F_publisher).map(Publisher(_))
      val note = fields.get(F_note).map(Note(_))
      InProceedings(author, title, bookTitle, year,
        editor, volumeOrNumber, series, pages, address, month, organization, publisher, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    inProceedings(entryKey)
  }
}

/**
  * Required fields: title
  * Optional fields: author, organization, address, edition, month, year, note
  */
case class Manual(title: Title,
                  author: Option[Author], organization: Option[Organization],
                  address: Option[Address], edition: Option[Edition], month: Option[Month],
                  year: Option[Year], note: Option[Note],
                  entryKey: EntryKey) extends Entry {
  val label = ET.Manual
}

object Manual {
  def apply(entry: CFEntry): Manual = {
    val manual = {
      val fields = entry.fields
      val title = Title(fields(F_title))
      val author = fields.get(F_author).map(Author(_))
      val organization = fields.get(F_organization).map(Organization(_))
      val address = fields.get(F_address).map(Address(_))
      val edition = fields.get(F_editor).map(Edition(_))
      val month = fields.get(F_month).map(Month(_))
      val year = fields.get(F_year).map(Year(_))
      val note = fields.get(F_note).map(Note(_))
      Manual(title, author, organization, address, edition, month, year, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    manual(entryKey)
  }
}

/**
  * Required fields: author, title, school, year.
  * Optional fields: type, address, month, note.
  */
case class MastersThesis(author: Author, title: Title, school: School, year: Year,
                         bType: Option[BType], address: Option[Address],
                         month: Option[Month], note: Option[Note],
                         entryKey: EntryKey) extends Entry {
  val label = ET.MastersThesis
}

object MastersThesis {

  val mastersThesisGen = LabelledGeneric[MastersThesis]

  import PhdThesis.phdThesisGen

  def apply(entry: CFEntry): MastersThesis = mastersThesisGen.from(phdThesisGen.to(PhdThesis(entry)))
}

/**
  * Optional fields: author, title, howpublished, month, year, note.
  */
case class Misc(author: Option[Author], title: Option[Title], howPublished: Option[HowPublished],
                month: Option[Month], year: Option[Year], note: Option[Note],
                entryKey: EntryKey) extends Entry {
  val label = ET.MastersThesis
}

object Misc {
  def apply(entry: CFEntry): Misc = {
    val misc = {
      val fields = entry.fields
      val author = fields.get(F_author).map(Author(_))
      val title = fields.get(F_title).map(Title(_))
      val howPublished = fields.get(F_howpublished).map(HowPublished(_))
      val month = fields.get(F_month).map(Month(_))
      val year = fields.get(F_year).map(Year(_))
      val note = fields.get(F_note).map(Note(_))
      Misc(author, title, howPublished, month, year, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    misc(entryKey)
  }
}

/**
  * Required fields: author, title, school, year.
  * Optional fields: type, address, month, note.
  */
case class PhdThesis(author: Author, title: Title, school: School, year: Year,
                     bType: Option[BType], address: Option[Address],
                     month: Option[Month], note: Option[Note],
                     entryKey: EntryKey) extends Entry {
  val label = ET.PhdThesis
}

object PhdThesis {
  val phdThesisGen = LabelledGeneric[PhdThesis]

  def apply(entry: CFEntry): PhdThesis = {
    val phdThesis = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val title = Title(fields(F_author))
      val school = School(fields(F_school))
      val year = Year(fields(F_year))
      val bType = fields.get(F_type).map(BType(_))
      val address = fields.get(F_address).map(Address(_))
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      PhdThesis(author, title, school, year, bType, address, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    phdThesis(entryKey)
  }
}

/**
  * Required fields: title, year.
  * Optional fields: editor, volume or number, series, address, month, organization, publisher, note.
  */
case class Proceedings(title: Title, year: Year,
                       editor: Option[Editor], volumeOrNumber: Option[VolumeOrNumber],
                       series: Option[Series], address: Option[Address],
                       month: Option[Month], organization: Option[Organization],
                       publisher: Option[Publisher], note: Option[Note],
                       entryKey: EntryKey) extends Entry {
  val label = ET.Proceedings
}

object Proceedings {
  def apply(entry: CFEntry): Proceedings = {
    val proceedings = {
      val fields = entry.fields
      val title = Title(fields(F_title))
      val year = Year(fields(F_year))
      val editor = fields.get(F_editor).map(Editor(_))
      val volumeOrNumber = {
        val volume = fields.get(F_volume).map(Volume(_))
        val number = fields.get(F_number).map(Number(_))
        (volume, number) match {
          case (Some(v), Some(n)) => {
            reportWarn(s"${v}:${n}")
            Some(Left(v))
          }
          case (Some(v), None) => Some(Left(v))
          case (None, Some(n)) => Some(Right(n))
          case (None, None) => None
        }
      }
      val series = fields.get(F_series).map(Series(_))
      val address = fields.get(F_address).map(Address(_))
      val month = fields.get(F_month).map(Month(_))
      val organization = fields.get(F_organization).map(Organization(_))
      val publisher = fields.get(F_publisher).map(Publisher(_))
      val note = fields.get(F_note).map(Note(_))
      Proceedings(title, year, editor, volumeOrNumber, series, address, month, organization, publisher, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    proceedings(entryKey)
  }
}

/**
  * Required fields: author, title, institution, year.
  * Optional fields: type, address, month, note.
  */
case class TechReport(author: Author, title: Title, institution: Institution, year: Year,
                      bType: Option[BType], address: Option[Address],
                      month: Option[Month], note: Option[Note],
                      entryKey: EntryKey) extends Entry {
  val label = ET.TechReport
}

object TechReport {
  def apply(entry: CFEntry): TechReport = {
    val techReport = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val title = Title(fields(F_title))
      val institution = Institution(fields(F_institution))
      val year = Year(fields(F_year))
      val bType = fields.get(F_type).map(BType(_))
      val address = fields.get(F_address).map(Address(_))
      val month = fields.get(F_month).map(Month(_))
      val note = fields.get(F_note).map(Note(_))
      TechReport(author, title, institution, year, bType, address, month, note, _: EntryKey)
    }
    val entryKey = entry.entryKey
    techReport(entryKey)
  }
}

/**
  * Required fields: author, title, note.
  * Optional fields: month, year.
  */
case class UnPublished(author: Author, title: Title, note: Note,
                       month: Option[Month], year: Option[Year],
                       entryKey: EntryKey) extends Entry {
  val label = ET.UnPublished
}

object UnPublished {
  def apply(entry: CFEntry): UnPublished = {
    val unPublished = {
      val fields = entry.fields
      val author = Author(fields(F_author))
      val title = Title(fields(F_title))
      val note = Note(fields(F_note))
      val month = fields.get(F_month).map(Month(_))
      val year = fields.get(F_year).map(Year(_))
      UnPublished(author, title, note, month, year, _: EntryKey)
    }
    val entryKey = entry.entryKey
    unPublished(entryKey)
  }
}