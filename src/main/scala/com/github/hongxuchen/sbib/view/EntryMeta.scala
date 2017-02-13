package com.github.hongxuchen.sbib.view

import com.github.hongxuchen.sbib.core.ET

import scala.reflect.runtime.universe.Symbol

class EntryMeta(entryType: ET) {

  import macros.FieldsInfo._

  val fields = entryType match {
    case ET.Article => caseClassFields[Article]
    case ET.Book => caseClassFields[Book]
    case ET.Booklet => caseClassFields[Booklet]
    case ET.Conference => caseClassFields[Conference]
    case ET.InBook => caseClassFields[InBook]
    case ET.InCollection => caseClassFields[InCollection]
    case ET.InProceedings => caseClassFields[InProceedings]
    case ET.Manual => caseClassFields[Manual]
    case ET.MastersThesis => caseClassFields[MastersThesis]
    case ET.Misc => caseClassFields[Misc]
    case ET.PhdThesis => caseClassFields[PhdThesis]
    case ET.Proceedings => caseClassFields[Proceedings]
    case ET.TechReport => caseClassFields[TechReport]
    case ET.UnPublished => caseClassFields[UnPublished]
  }
  val entryKeyName = "entryKey"

  def msg = {
    val (optional, required) = fields.filter(!neglected(_)).partition(isOptional)
    s"""
       |entry: ${entryType}
       |required field(s): ${required.map(_.name).mkString(", ")}
       |optional fields: ${optional.map(_.name).mkString(", ")}""".stripMargin
  }

  def isOptional(symbol: Symbol): Boolean = {
    symbol.info.toString.startsWith("Option")
  }

  def neglected(symbol: Symbol): Boolean = {
    symbol.name.toString == entryKeyName
  }

}
