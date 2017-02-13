package com.github.hongxuchen.sbib.core

case class SortConfig(sortBy: String, asc: Boolean = true, ignoreCase: Boolean = true)

object SortConfig {
  def apply(asc: Boolean)(ignoreCase: Boolean)(s: String): SortConfig = {
    val ss = s.trim.toLowerCase
    SortConfig(ss, asc, ignoreCase)
  }
}

class Sorting(rawDB: RawDB) {
  // TODO multiple fields version
  // I need to write correct sorting
  def sortIt(sortConfig: SortConfig) = {
    val sortBy = sortConfig.sortBy
    // TODO implement
    ???
  }

}
