package com.github.hongxuchen.sbib.core

import enumeratum._

import scala.collection.immutable.IndexedSeq

sealed trait ET extends EnumEntry

object ET extends Enum[ET] {

  // TODO deal with customized Entry Type?

  override def values: IndexedSeq[ET] = findValues

  case object Article extends ET

  case object Book extends ET

  case object Booklet extends ET

  case object Conference extends ET

  case object InBook extends ET

  case object InCollection extends ET

  case object InProceedings extends ET

  case object Manual extends ET

  case object MastersThesis extends ET

  case object Misc extends ET

  case object PhdThesis extends ET

  case object Proceedings extends ET

  case object TechReport extends ET

  case object UnPublished extends ET

}