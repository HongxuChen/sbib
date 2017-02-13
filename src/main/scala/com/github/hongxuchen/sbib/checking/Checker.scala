package com.github.hongxuchen.sbib.checking

import com.github.hongxuchen.sbib.errors.CheckError

trait Checker {

  def result: Seq[CheckError]

  def info: String
}
