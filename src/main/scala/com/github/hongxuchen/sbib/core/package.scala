package com.github.hongxuchen.sbib

import cats.data.Validated
import com.github.hongxuchen.sbib.errors.RawError

import scala.collection.immutable.ListMap

package object core {

  type RawFieldMap = ListMap[String, String]
  type CFFieldMap = Map[String, String]

  type RawEntryMap = Map[EntryKey, RawEntry]
  type RawEntries = Seq[(EntryKey, RawEntry)]
  type CFEntries = Seq[(EntryKey, CFEntry)]
  type ValidationRawDB = Validated[RawError, RawDB]

}
