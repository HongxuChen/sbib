package com.github.hongxuchen.sbib.core

case class EntryKey(s: String) {
  override def equals(that: Any) = that match {
    case te: EntryKey => s.toLowerCase == te.s.toLowerCase
    case _ => false
  }

  override def hashCode = s.toLowerCase.hashCode

}
