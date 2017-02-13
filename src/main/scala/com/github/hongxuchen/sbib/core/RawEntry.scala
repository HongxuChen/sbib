package com.github.hongxuchen.sbib.core

import com.github.hongxuchen.sbib.utils.Graph
import FT._

case class RawDB(rawEntries: RawEntries) {
  lazy val entryMap = rawEntries.toMap

  // after this, NO order is preserved from the original entries
  def cr: CFDB = {
    val refee2ref = for {
      (_, e) <- rawEntries
      refee <- e.getCrossRef(entryMap).toList
    } yield refee -> e
    val sortedEntries = Graph.tsort(refee2ref)
    sortedEntries match {
      case Nil => CFDB {
        rawEntries.map { case (k, rawEntry) =>
          k -> rawEntry.toCFEntry
        }
      }
      case head :: tail => {
        // new_{n+1} = updated(new_n, old_{n+1}) where new_0 = old_0
        val decfed = tail.scanLeft(head.toCFEntry) {
          (acc, cur) => {
            val fieldMap = acc.fields ++ cur.fields - F_crossref
            CFEntry(cur.entryKey, cur.entryType, fieldMap)
          }
        }
        // deliberately separate the procedure
        val decfedSeq = decfed.map(e => e.entryKey -> e)
        // update
        val newEntries = {
          val remaining = rawEntries diff sortedEntries.map(e => e.entryKey -> e)
          remaining.map(m => m._1 -> m._2.toCFEntry) ++ decfedSeq
        }
        CFDB(newEntries)
      }
    }
  }

}

case class RawEntry(entryKey: EntryKey, entryType: ET, fields: RawFieldMap) {

  def toCFEntry: CFEntry = {
    require(!fields.contains(FT.F_crossref))
    CFEntry(entryKey, entryType, fields)
  }


  // used to represent original value, therefore the form is unique
  // TODO handle multiple line string representation
  override def toString: String = {
    s"""@${entryType}{${entryKey.s},
       |${fields.map { case (f, v) => f"  ${f}%-16s  =  ${v}%20s," }.mkString("\n")}
       |}
    """.stripMargin
  }

  @throws[NoSuchElementException]
  def getCrossRef(rawEntryMap: RawEntryMap): Option[RawEntry] =
    fields.get(F_crossref) map { s =>
      val k = EntryKey(s.toLowerCase)
      rawEntryMap(k)
    }

}

case class CFDB(cFEntries: CFEntries) {
  lazy val entryMap = cFEntries.toMap
}

case class CFEntry(entryKey: EntryKey, entryType: ET, fields: CFFieldMap)