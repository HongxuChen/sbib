package com.github.hongxuchen.sbib.statistics

import com.github.hongxuchen.sbib.core.{ET, EntryKey, RawDB, RawEntry}

class Statistics(rawDB: RawDB) {

  lazy val size = rawDB.rawEntries.size

  lazy val groups: Map[ET, Seq[(EntryKey, RawEntry)]] = {
    rawDB.rawEntries.groupBy { case (k, e) => e.entryType }
  }

  lazy val groupSize: Map[ET, Int] = groups.map { case (k, es) => (k, es.size) }

  def info(verbose: Boolean): String = {
    f"""${"All"}%-15s${size}%20s
       |${groupSize.map { case (et, n) => f"${et}%-15s${n}%20s" }.mkString("\n")}
    """.stripMargin
  }

}
