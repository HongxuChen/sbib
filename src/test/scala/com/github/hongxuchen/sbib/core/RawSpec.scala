package com.github.hongxuchen.sbib.core

import com.github.hongxuchen.sbib.SBibSpec

import scala.collection.immutable.ListMap

class RawSpec extends SBibSpec {

  val entry1 = {
    val entryKey = EntryKey("key1")
    val entryType = ET.Article
    val fields = ListMap(
      "crossref" -> "Key5",
      "author" -> "H.C.",
      "year" -> "2001",
      "title" -> "title1"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val entry2 = {
    val entryKey = EntryKey("key2")
    val entryType = ET.TechReport
    val fields = ListMap(
      "crossref" -> "key1",
      "year" -> "2003",
      "title" -> "title2",
      "month" -> "May"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val entry3 = {
    val entryKey = EntryKey("key3")
    val entryType = ET.InProceedings
    val fields = ListMap(
      "crossref" -> "Key1",
      "year" -> "2003",
      "title" -> "title3",
      "month" -> "May"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val entry4 = {
    val entryKey = EntryKey("key4")
    val entryType = ET.InProceedings
    val fields = ListMap(
      "crossref" -> "key_1",
      "year" -> "2003",
      "title" -> "title3",
      "month" -> "May"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val entry5 = {
    val entryKey = EntryKey("key5")
    val entryType = ET.Article
    val fields = ListMap(
      "title" -> "title5"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val entry6 = {
    val entryKey = EntryKey("key5")
    val entryType = ET.Article
    val fields = ListMap(
      "crossref" -> "key2",
      "title" -> "title6"
    )
    RawEntry(entryKey, entryType, fields)
  }

  val correctDB = RawDB {
    Seq(entry1, entry2, entry3, entry5).map(e => (e.entryKey, e))
  }

  val circularDB = RawDB {
    Seq(entry1, entry2, entry6).map(e => e.entryKey -> e)
  }

  val norefDB = RawDB {
    Seq(entry1, entry2, entry3, entry4).map(e => e.entryKey -> e)
  }

  it should "throw when no crossref key found" in {
    assertThrows[NoSuchElementException] {
      norefDB.cr
    }
  }

  "raw db" should "deal with crossref" in {
    val entryMap = correctDB.cr.entryMap
    val entries = List(
      (EntryKey("key5"), CFEntry(EntryKey("key5"), ET.Article, Map("title" -> "title5"))),
      (EntryKey("key1"), CFEntry(EntryKey("key1"), ET.Article, Map("title" -> "title1", "author" -> "H.C.", "year" -> "2001"))),
      (EntryKey("key2"), CFEntry(EntryKey("key2"), ET.TechReport, Map("author" -> "H.C.", "year" -> "2003", "title" -> "title2", "month" -> "May"))),
      (EntryKey("key3"), CFEntry(EntryKey("key3"), ET.InProceedings, Map("author" -> "H.C.", "year" -> "2003", "title" -> "title3", "month" -> "May"))))
    import org.scalatest.Inspectors._
    import org.scalatest.PartialFunctionValues._
    forAll(entries) { case (k, e) => entryMap.valueAt(k) should equal(e) }
  }

  it should "throw error for circular crossref" in {
    // TODO figure out why RuntimeException
    assertThrows[RuntimeException](circularDB.cr)
  }

}
