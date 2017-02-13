package com.github.hongxuchen

import java.nio.file.Paths

package object sbib {

  private lazy val inputDir = getClass.getResource("/bibs").toURI.getPath

  def getInputFile(fn: String): String = Paths.get(inputDir, fn).toString

}
