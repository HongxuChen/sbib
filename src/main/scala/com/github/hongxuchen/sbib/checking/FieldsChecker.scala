package com.github.hongxuchen.sbib.checking

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import com.github.hongxuchen.sbib.core.{CFDB, CFEntry, ET, _}
import com.github.hongxuchen.sbib.errors.{CheckError, MissingFields}

class FieldsChecker(val cfDB: CFDB) extends Checker {

  type ValidationFI = Validated[String, String]

  type FieldsTy = Seq[AnyRef]

  val fieldsSpec: Map[ET, Seq[FieldsTy]] = {
    import ET._
    import com.github.hongxuchen.sbib.core.FT._
    Map(
      Article -> Seq(
        Seq(F_author, F_title, F_journal, F_year),
        Seq(F_volume, F_number, F_pages, F_month, F_note)
      ),
      Book -> Seq(
        Seq((F_author, F_editor), F_title, F_publisher, F_year),
        Seq((F_volume, F_number), F_series, F_address, F_edition, F_month, F_note)
      ),
      Booklet -> Seq(
        Seq(F_title),
        Seq(F_author, F_howpublished, F_address, F_month, F_year, F_note)
      ),
      Conference -> Seq(
        Seq(F_author, F_title, F_booktitle, F_year),
        Seq(F_editor, (F_volume, F_number), F_series, F_pages, F_address, F_month, F_organization, F_publisher, F_note)
      ),
      InBook -> Seq(
        Seq((F_author, F_editor), F_title, List(F_chapter, F_pages), F_publisher, F_year),
        Seq((F_volume, F_number), F_series, F_type, F_address, F_edition, F_month, F_note)
      ),
      InCollection -> Seq(
        Seq(F_author, F_title, F_booktitle, F_publisher, F_year),
        Seq(F_editor, (F_volume, F_number), F_series, F_type, F_chapter, F_pages, F_address, F_edition, F_month, F_note)
      ),
      InProceedings -> Seq(
        Seq(F_author, F_title, F_booktitle, F_year),
        Seq(F_editor, (F_volume, F_number), F_series, F_pages, F_address, F_month, F_organization, F_publisher, F_note)
      ),
      Manual -> Seq(
        Seq(F_title),
        Seq(F_author, F_organization, F_address, F_edition, F_month, F_year, F_note)
      ),
      MastersThesis -> Seq(
        Seq(F_author, F_title, F_school, F_year),
        Seq(F_type, F_address, F_month, F_note)
      ),
      Misc -> Seq(
        Seq.empty,
        Seq(F_author, F_title, F_howpublished, F_month, F_year, F_note)
      ),
      PhdThesis -> Seq(
        Seq(F_author, F_title, F_school, F_year),
        Seq(F_type, F_address, F_month, F_note)
      ),
      Proceedings -> Seq(
        Seq(F_title, F_year),
        Seq(F_editor, (F_volume, F_number), F_series, F_address, F_month, F_organization, F_publisher, F_note)
      ),
      TechReport -> Seq(
        Seq(F_author, F_title, F_institution, F_year),
        Seq(F_type, F_address, F_month, F_note)
      ),
      UnPublished -> Seq(
        Seq(F_author, F_title, F_note),
        Seq(F_month, F_year)
      )
    )
  }

  lazy val resultInteral: Seq[MissingFields] = for {
    (_, entry) <- cfDB.cFEntries
    res <- check(entry)
  } yield res


  def result: Seq[CheckError] = resultInteral

  override def info: String = {
    // TODO it's possible that there exists duplicated key AND malformed fields
    resultInteral.map { mf =>
      s"key:${mf.cfEntry.entryKey.s}\t${mf.fields.mkString("[", ";", "]")}"
    }.mkString("\n")
  }

  private def check(cFEntry: CFEntry) = {
    val fields = cFEntry.fields
    val required = fieldsSpec(cFEntry.entryType).head
    val missings = for {
      r <- required
      f <- Seq(checkImpl(fields)(r)).collect({ case Invalid(f) => f })
    } yield f
    if (missings.isEmpty) {
      None
    } else {
      Some(MissingFields(cFEntry, missings))
    }
  }

  private def checkImpl(fields: CFFieldMap)(r: AnyRef): ValidationFI = r match {
    case s: String => fields.get(s) match {
      case Some(_) => Valid(s)
      case None => Invalid(s)
    }
    case (fst: String, snd: String) => {
      val repr = s"${fst}/${snd}"
      fields.get(fst).orElse(fields.get(snd)) match {
        case Some(_) => Valid(repr)
        case None => Invalid(repr)
      }
    }
    case l: List[_] => {
      import shapeless.syntax.typeable._
      val repr = l.mkString("[", ";", "]")
      l.cast[List[String]] match {
        case Some(ls) => if (ls.forall(fields.get(_).isEmpty)) {
          Invalid(repr)
        } else {
          Valid(repr)
        }
        case None => Invalid(repr)
      }
    }
    case f => Invalid(f.toString)
  }

}
