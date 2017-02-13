package com.github.hongxuchen.sbib.checking

import cats.data.Validated.Valid
import com.github.hongxuchen.sbib.SBibSpec
import com.github.hongxuchen.sbib.core.RawDB

class DupSpec extends SBibSpec {

  "key duplicates" should "be caught" in {
    val s =
      """@inproceedings{Barthe:2004:SIF:1009380.1009669,
        | author = {Barthe, Gilles and D'Argenio, Pedro R. and Rezk, Tamara},
        | title = {Secure Information Flow by Self-Composition},
        | booktitle = {Proceedings of the 17th IEEE Workshop on Computer Security Foundations},
        | series = {CSFW '04},
        | year = {2004},
        | isbn = {0-7695-2169-X},
        | pages = {100--},
        | url = {http://dx.doi.org/10.1109/CSFW.2004.17},
        | doi = {10.1109/CSFW.2004.17},
        | acmid = {1009669},
        | publisher = {IEEE Computer Society},
        | address = {Washington, DC, USA},
        |}
        |
        |@techreport{barthe:2004:SIF:1009380.1009669,
        | author = {Barthe, Gilles and D'Argenio, Pedro R. and Rezk, Tamara},
        | title = {Secure Information Flow by Self-Composition},
        | booktitle = {Proceedings of the 17th IEEE Workshop on Computer Security Foundations},
        | series = {CSFW '04},
        | year = {2004},
        | isbn = {0-7695-2169-X},
        | pages = {100--},
        | url = {http://dx.doi.org/10.1109/CSFW.2004.17},
        | doi = {10.1109/CSFW.2004.17},
        | acmid = {1009669},
        | publisher = {IEEE Computer Society},
        | address = {Washington, DC, USA},
        |}
      """.stripMargin

    import com.github.hongxuchen.sbib.core.DBManager._
    val db = parseString(s).asInstanceOf[Valid[RawDB]].a
    val sanitizer = new DupChecker(db)
    sanitizer.keyDuplicates should have size 1
  }

}
