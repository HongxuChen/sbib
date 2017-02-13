package com.github.hongxuchen.sbib.statistics

import com.github.hongxuchen.sbib.SBibSpec
import com.github.hongxuchen.sbib.core.ET

class StatisticsSpec extends SBibSpec {

  import com.github.hongxuchen.sbib.inputs.FileDB._

  val db = getSuccessRes(correctDB)
  val statistics = new Statistics(db)

  "statistics" should "be correct" in {
    statistics.size shouldBe 80
  }

  it should "be grouped" in {
    statistics.groupSize shouldBe {
      Map(
        ET.InProceedings -> 40,
        ET.Misc -> 5,
        ET.Article -> 21,
        ET.Book -> 4,
        ET.TechReport -> 1,
        ET.Booklet -> 1,
        ET.InBook -> 7,
        ET.PhdThesis -> 1
      )
    }
  }

}
