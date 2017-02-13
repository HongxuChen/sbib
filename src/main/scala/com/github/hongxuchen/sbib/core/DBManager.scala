package com.github.hongxuchen.sbib.core

import java.io.{File => JFile}
import java.nio.file.NoSuchFileException

import cats.data.Validated.{Invalid, Valid}
import com.github.hongxuchen.sbib.errors.BibFileNotFound
import com.typesafe.scalalogging.LazyLogging

object DBManager extends LazyLogging {

  def db2EntryMap(validationDB: ValidationRawDB): RawEntryMap = validationDB match {
    case Valid(db) => db.entryMap
    case Invalid(e) => throw new RuntimeException(e.msg)
  }

  def toCFDB(validationDB: ValidationRawDB): CFDB = validationDB match {
    case Valid(db) => db.cr
    case Invalid(e) => throw new RuntimeException(e.msg)
  }

  def parseFile(fs: String*)(implicit dummyImplicit: DummyImplicit): ValidationRawDB = {
    import better.files._
    try {
      require(fs.nonEmpty, "file list should not be empty")
      val s = fs.map(f => File(f).contentAsString).mkString("\n")
      parseString(s)
    } catch {
      case ef: NoSuchFileException => {
        val msg = s"${ef.getFile}"
        Invalid(BibFileNotFound(msg))
      }
    }
  }

  def parseFile(fs: Seq[JFile]): ValidationRawDB = parseFile(fs.map(_.toString): _*)

  def parseString(s: String): ValidationRawDB = {
    import com.github.hongxuchen.sbib.parsing.RawParser._
    parseDB(s)
  }

}

