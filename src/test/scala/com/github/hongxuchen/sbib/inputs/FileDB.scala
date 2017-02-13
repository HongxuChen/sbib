package com.github.hongxuchen.sbib.inputs

import com.github.hongxuchen.sbib.parsing.RawParser.bibP
import fastparse.core.{Parsed, Parser}
import com.github.hongxuchen.sbib.getInputFile

object FileDB {

  val correctDB = {
    import better.files._
    val f = File(getInputFile("correct.bib"))
    val s = f.contentAsString
    bibP.parse(s)
  }

  def getSuccessRes[T, Elem, Repr](p: Parsed[T, Elem, Repr]): T = {
    p.get.value
  }

  def getFailureRes[T, Elem, Repr](p: Parsed[T, Elem, Repr]): Parser[_, Elem, Repr] = p match {
    case Parsed.Failure(f, _, _) => f
    case Parsed.Success(t, elem) => throw new RuntimeException("should be a failure")
  }

}
