package com.github.hongxuchen.sbib.utils

object Utility {

  implicit class BibStringOps(s: String) {
    def normalized = s.replaceAll("\\s+", " ")
  }

  object ReportLevel extends Enumeration {
    type Ty = Value
    val NORMAL, WARN, ERROR = Value
  }

  private def report(msg: AnyRef, level: ReportLevel.Ty) = {
    import ReportLevel._
    level match {
      case NORMAL => println(msg)
      case _ => Console.err.println(s"[${level}] ${msg}")
    }
  }

  def reportNormal(msg: AnyRef) = report(msg, ReportLevel.NORMAL)

  def reportWarn(msg: AnyRef) = report(msg, ReportLevel.WARN)

  def reportError(msg: AnyRef) = report(msg, ReportLevel.ERROR)

}