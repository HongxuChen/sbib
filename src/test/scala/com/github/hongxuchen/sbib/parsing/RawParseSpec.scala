package com.github.hongxuchen.sbib.parsing

import com.github.hongxuchen.sbib.SBibSpec

class RawParseSpec extends SBibSpec {

  import com.github.hongxuchen.sbib.parsing.RawParser._
  import fastparse.all._
  import org.scalatest.Inspectors._

  "string bulk" should "pass" in {

    val simple1 = List(
      "{yes }",
      """{
        |test
        |it}""".stripMargin,
      "google's",
      """
        |bull shit
      """.stripMargin, "lol\n",
      "\"yes \"",
      """"
        |test
        |it"""".stripMargin
    )

    val simple2 = List(
      // TODO what's this
      "{\'{a}}",
      "{\'abc}",
      "{\'{abc}}",
      "\'a",
      // easy
      "u1234",
      """{\'\"{a}}""",
      """\n\t\n""",
      """{\'{a}}""",
      """\'a""",
      "{{tom}:jack}",
      "{{International Workshop on {\"Scripts\"} to Programs}}",
      """{Sj\"{o}berg, Vilhelm}"""
    )
    forAll(simple1 ++ simple2) { b => (StringBulk.! ~ End).parse(b) shouldBe a[Parsed.Success[_]] }

    val wrong = List(
      // TODO what's this!!!
      "{\'{\"a}}",
      "{\"\'}",
      // easy
      "abcdef,gh",
      "\"abc",
      "{abc{}",
      "\"abc\\}"
    )
    forAll(wrong) { s => (StringBulk ~ End).parse(s) shouldBe a[Parsed.Failure] }
  }

  "entry key" should "allow" in {
    val keys = List("Tim:stop11")

    forAll(keys) { k => (entryKeyP ~ End).parse(k) shouldBe a[Parsed.Success[_]] }
  }

  "entry name" should "match" in {
    val names = List("article", "Article", "aRtIcle")

    forAll(names) { n => (entryNameP ~ End).!.parse(n).get.value.toLowerCase shouldBe "article" }

    val wrongNames = List("inp", "", "artlcle", " article")
    forAll(wrongNames) { n => (entryNameP ~ End).parse(n) shouldBe a[Parsed.Failure] }
  }

  "field name" should "match" in {
    val names = List("acmid", "ACMid")
    forAll(names) { n => (fieldNameP ~ End).!.parse(n).get.value.toLowerCase shouldBe "acmid" }

    val all = List("issue_date", "burn_them_all")
    forAll(all) { n => (fieldNameP ~ End).parse(n) shouldBe a[Parsed.Success[_]] }
    val wrongNames = List("pod_")
    forAll(wrongNames) { n => (fieldNameP ~ End).parse(n) shouldBe a[Parsed.Failure] }
  }

  "field item" should "be of form 'key = value'" in {
    val fiList = List(
      "Year = {1999}\n",
      "Year =   {1998}",
      "Year=\"1998" +
        "\"",
      """author = {J. F. K,
        | KFC}""".stripMargin,
      "author = {Bohannon, Aaron and Pierce, Benjamin C. and Sj\\\"{o}berg, Vilhelm and Weirich, Stephanie and Zdancewic, Steve}",
      """author="Boudol, G{\'e}rard"""",
      """author="Boudol, G{\'e}rard and Kolund{\v{z}}ija, Marija"""",
      "url = {\\url{https://developer.android.com/reference/android/os/Binder.html}}"
    )
    forAll(fiList) { f => (fieldItemP ~ End).parse(f) shouldBe a[Parsed.Success[_]] }
    val wrongFiList = List(
      " Year = \"1999\"")
    forAll(wrongFiList) { f => (fieldItemP ~ End).parse(f) shouldBe a[Parsed.Failure] }
  }

  "field items" should "contain a list of field items" in {
    val fisList = List(
      """Year = {1998},
        |
        | author = "H, C and C, H"""".stripMargin,
      """title = {Gradual Information Flow Typing},
        |""".stripMargin
    )
    forAll(fisList) { fis => (fieldItemsP ~ End).parse(fis) shouldBe a[Parsed.Success[_]] }
  }

  "entry item" should "contain entryName, entryKey, field items" in {
    val eiList = List(
      """@inproceedings{Tim:stop11,
        |title = {Gradual Information Flow Typing},}""".stripMargin,
      """@inproceedings{Tim:stop11,
        |title = {Gradual Information Flow Typing},
        |author = {Disney, Tim and Flanagan, Cormac},
        |year = {2011},
        |booktitle = {International Workshop on Scripts to Programs},
        |}""".stripMargin
    )
    forAll(eiList) { e =>
      (entryItem ~ End).parse(e) shouldBe a[Parsed.Success[_]]
    }

    val wrongEiList = List(
      // no field items
      """@inproceedings{Tim:stop11,
        |}""".stripMargin,
      """
        |@Report{haha,
        |title = {xxx}
        |}
      """.stripMargin
    )
    forAll(wrongEiList) { e =>
      (entryItem ~ End).parse(e) shouldBe a[Parsed.Failure]
    }
  }

  "entry items" should "be a list of entry items" in {
    val entryItems =
      """@inproceedings{Tim:stop11,
        |title = {Gradual Information Flow Typing},
        |author = {Disney, Tim and Flanagan, Cormac},
        |year = {2011},
        |booktitle = {International Workshop on Scripts to Programs},
        |}
        |
        |@inproceedings{Swamy:2006:MPU:1155442.1155678,
        | author = {Swamy, Nikhil and Hicks, Michael and Tse, Stephen and Zdancewic, Steve},
        | title = {Managing Policy Updates in Security-Typed Languages},
        | booktitle = {Proceedings of the 19th IEEE Workshop on Computer Security Foundations},
        | series = {CSFW '06},
        | year = {2006},
        | isbn = {0-7695-2615-2},
        | pages = {202--216},
        | numpages = {15},
        | url = {http://dx.doi.org/10.1109/CSFW.2006.17},
        | doi = {10.1109/CSFW.2006.17},
        | acmid = {1155678},
        | publisher = {IEEE Computer Society},
        | address = {Washington, DC, USA},
        | }""".stripMargin

    entryItems.parse(entryItems) shouldBe a[Parsed.Success[_]]

  }

  "bib database" should "pass" in {
    import com.github.hongxuchen.sbib.inputs.FileDB._
    correctDB shouldBe a[Parsed.Success[_]]
  }

}
