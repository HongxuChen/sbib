package com.github.hongxuchen.sbib.checking

import com.github.hongxuchen.sbib.core.{EntryKey, RawDB, RawEntry}
import com.github.hongxuchen.sbib.core.{RawDB, RawEntry}
import com.github.hongxuchen.sbib.errors.{CheckError, DuplicateKeys}

// check whether should be based on CFDB
class DupChecker(val rawDB: RawDB) extends Checker {

  lazy val keyDuplicates: Map[EntryKey, Seq[RawEntry]] = {
    val grouped = rawDB.rawEntries.groupBy { case (k, _) => k }
    for {
      (k, kel) <- grouped
      if kel.length > 1
      el = kel.map(_._2)
    } yield (k, el)
  }

  override def result: Seq[CheckError] =
    keyDuplicates.map { case (k, entries) => DuplicateKeys(k, entries) }.toSeq


  override def info: String = {
    keyDuplicates.map { case (k, entries) =>
      s"""
         |key=${k}
         |entries:
         |${entries.mkString("\n")}
        """.stripMargin
    }.mkString("\n")
  }
}
