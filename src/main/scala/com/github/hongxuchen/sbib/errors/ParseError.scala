package com.github.hongxuchen.sbib.errors

import com.github.hongxuchen.sbib.core.{CFEntry, EntryKey, RawEntry}
import com.github.hongxuchen.sbib.core.{CFEntry, RawEntry}

sealed trait SBibError

case class FieldMissing(entryKey: EntryKey, msg: String) extends SBibError

case class MalformedField(entryKey: EntryKey, fieldName: String, msg: String) extends SBibError

case class UnknownEntry(entryKey: EntryKey, entryType: String) extends SBibError


///////////////////////////////////////////////////////////////////////////

sealed trait CheckError

case class MissingFields(cfEntry: CFEntry, fields: Seq[String]) extends CheckError

case class DuplicateKeys(entryKey: EntryKey, dupEntries: Seq[RawEntry]) extends CheckError

//////////////////////////////////////////////////////////////////////////

sealed trait RawError {
  def msg: String
}

case class RawParseError(expected: String, index: Int, msg: String) extends RawError

case class BibFileNotFound(msg: String) extends RawError

//////////////////////////////////////////////////////////////////////////

object Handling {

  def fieldRequire(cond: Boolean)(field: String, msg: String): Unit = {
    if (!cond) {
      throw new MalformedFieldExcept(field, msg)
    }
  }

  class MonthKErrorExcept(val s: String) extends Exception

  class MalformedFieldExcept(val field: String, val msg: String) extends Exception

}