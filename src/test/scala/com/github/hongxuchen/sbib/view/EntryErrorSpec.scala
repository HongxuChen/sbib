package com.github.hongxuchen.sbib.view

import cats.data.Validated.Invalid
import com.github.hongxuchen.sbib.core.DBManager._
import com.github.hongxuchen.sbib.core.EntryKey
import com.github.hongxuchen.sbib.errors._
import EntryGenerator._
import com.github.hongxuchen.sbib.SBibSpec

class EntryErrorSpec extends SBibSpec {

  "parsing error" should "be caught" in {
    val s =
      """
        |@inproceedings{Kennedy2015,
        |author={M. Kennedy and R. Sulaiman},
        |booktitle={Electrical Engineering and Informatics (ICEEI), 2015 International Conference on},
        |title={Following the Wi-Fi breadcrumbs: Network based mobile application privacy threats},
        |year={2015},
        |pages={265-270},
        |month={Aag},}
      """.stripMargin
    val db = parseString(s)
    val rawEntry = toCFDB(db)
    val entry = genEntries(rawEntry).head
    entry shouldBe Invalid(MalformedField(EntryKey("Kennedy2015"), "month", "{Aag}"))
  }

  "bib file not found" should "be caught" in {
    val f = "/tmp/not_exists.bib"
    val db = parseFile(f)
    db shouldBe Invalid(BibFileNotFound(f))
  }


  "missing/malformed fields" should "be invalid" in {
    import com.github.hongxuchen.sbib.inputs.StringDB._
    val vdb = parseString(malformedFieldsDB)
    val rawDB = toCFDB(vdb)
    val entries = genEntries(rawDB)
    entries should contain {
      Invalid(FieldMissing(EntryKey("Swamy:2006:MPU:1155442.1155678"), "key not found: booktitle"))
    }
    entries should contain {
      Invalid(MalformedField(EntryKey("vanRest2014"), "personName", "familyName: Name(P.)"))
    }

  }

}
