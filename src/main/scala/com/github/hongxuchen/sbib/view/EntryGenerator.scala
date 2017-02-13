package com.github.hongxuchen.sbib.view

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.github.hongxuchen.sbib.core.{CFDB, CFEntry, ET, ValidationRawDB}
import com.github.hongxuchen.sbib.errors.Handling.MalformedFieldExcept
import com.github.hongxuchen.sbib.errors.{FieldMissing, MalformedField, SBibError, UnknownEntry}
import com.github.hongxuchen.sbib.utils.Utility._
import com.github.hongxuchen.sbib.writer.Show

object EntryGenerator {

  type ValidationEntry = Validated[SBibError, Entry]

  def fromDB(vdb: ValidationRawDB) = vdb match {
    case Valid(db) => {
      val entries = {
        genEntries(db.cr)
      }
      entries foreach {
        case entry: Valid[Entry] => {
          val shower = new Show()
          shower.show(entry.a)
        }
        case invalid: Invalid[SBibError] => invalid.e match {
          case UnknownEntry(entryKey, entryType) => {
            reportError(s"unknown ${entryType} for ${entryKey}")
          }
          case FieldMissing(entryKey, entryType) => {
            reportError(s"missing field for ${entryKey} of ${entryType}")
          }
          case MalformedField(entryKey, field, why) => {
            reportError(s"malformed field ${field} for ${entryKey}:\t${why}")
          }
        }
      }
    }
    case Invalid(e) => {
      reportError(e.msg)
    }
  }

  def genEntries(db: CFDB): Seq[ValidationEntry] = {
    db.cFEntries.map { case (_, e) => genEntry(e) }
  }

  def genEntry(raw: CFEntry): ValidationEntry = {
    try {
      // exception will be handled
      val entry: Entry = raw.entryType match {
        case ET.Article => Article(raw)
        case ET.Book => Book(raw)
        case ET.Booklet => Booklet(raw)
        case ET.Conference => Conference(raw)
        case ET.InBook => InBook(raw)
        case ET.InCollection => InCollection(raw)
        case ET.InProceedings => InProceedings(raw)
        case ET.Manual => Manual(raw)
        case ET.MastersThesis => MastersThesis(raw)
        case ET.Misc => Misc(raw)
        case ET.PhdThesis => PhdThesis(raw)
        case ET.Proceedings => Proceedings(raw)
        case ET.TechReport => TechReport(raw)
        case ET.UnPublished => UnPublished(raw)
      }
      Valid(entry)
    } catch {
      case e: NoSuchElementException => {
        Invalid(FieldMissing(raw.entryKey, e.getMessage))
      }
      case e: MalformedFieldExcept => {
        Invalid(MalformedField(raw.entryKey, e.field, e.msg))
      }
    }
  }
}