package com.github.hongxuchen.sbib.core

import java.io.File

import cats.data.Validated.{Invalid, Valid}
import com.github.hongxuchen.BuildInfo
import com.github.hongxuchen.sbib.checking.{DupChecker, FieldsChecker}
import com.github.hongxuchen.sbib.errors.{BibFileNotFound, RawParseError}
import com.github.hongxuchen.sbib.statistics.Statistics
import com.typesafe.scalalogging.LazyLogging
import com.github.hongxuchen.sbib.utils.Utility._
import scopt.OptionParser

object Driver extends LazyLogging {

  val defaultMode = "query"

  case class Config(file_ins: Seq[File] = Seq.empty,
                    file_out: String = "",
                    mode: String = defaultMode,
                    res_default: Boolean = false,
                    res_file: File = new File("."),
                    res_cmd: String = "", // res cmd
                    msg_q: Boolean = false, // silent
                    msg_v: Boolean = false, // verbose
                    sort_s: Boolean = false, // sort
                    sort_S: Boolean = false, // reverse sort
                    sort_A: String = "", // 0, a, A
                    item_x: File = new File("."), // aux file
                    item_X: String = "", // regex
                    item_cr: Boolean = false,
                    kgen_f: String = "", // format
                    kgen_F: Boolean = false, // turn on
                    kgen_k: Boolean = false, // short format keys,
                    kgen_K: Boolean = false, // long format keys
                    check_d: Boolean = false, // find duplicates
                    check_f: Boolean = false, // check fields
                    macro_m: File = new File("."), //
                    macro_M: File = new File("."), //
                    stat_s: Boolean = false // short
                   )

  private def files2DB(files: Seq[File]): ValidationRawDB = {
    if (files.isEmpty) {
      reportWarn(s"no input files, will read one line as input string")
      val s = io.StdIn.readLine()
      DBManager.parseString(s)
    } else {
      reportNormal(s"input files: ${files.mkString("[", ",", "]")}")
      DBManager.parseFile(files)
    }
  }

  def main(args: Array[String]): Unit = {
    val parser = parseCLI(args)
    parser.parse(args, Config()) match {
      case Some(config) => {
        files2DB(config.file_ins) match {
          case Valid(rawDB) => subCmds(rawDB, config)
          case Invalid(e) => e match {
            case BibFileNotFound(msg) => {
              reportError(s"${msg} not found")
            }
            case RawParseError(expected, index, msg) => {
              reportError(s"expected: ${expected}, error index:${index}\n${msg}")
            }
          }
        }
      }
      case None => {}
    }
  }

  def subCmds(rawDB: RawDB, config: Config) = {
    reportNormal(s"analyze in [${config.mode}] mode")
    if (config.mode == "stat") {
      val stats = new Statistics(rawDB)
      reportNormal(stats)
    } else if (config.mode == "check") {
      if (config.check_d) {
        val duplicates = new DupChecker(rawDB)
        reportNormal(duplicates.info)
      }
      if (config.check_f) {
        val cfRawDB = rawDB.cr
        val fieldsChecker = new FieldsChecker(cfRawDB)
        val msg = fieldsChecker.info
        if (msg.nonEmpty) {
          val banner = "Missing fields:"
          reportNormal(banner + "\n" + msg)
        }
      }
    } else if (config.mode == "rewrite") {

    } else {

    }
  }

  def parseCLI(args: Array[String]): OptionParser[Config] = {
    new scopt.OptionParser[Config](BuildInfo.name) {

      override def terminate(exitState: Either[String, Unit]): Unit = ()

      override def showUsageOnError = true

      head(BuildInfo.name, BuildInfo.version)

      help("help").abbr("h").text("prints this usage text and exit").action((_, c) => {
        sys.exit(0)
      })

      opt[Unit]("asc").action((_, c) => c.copy(sort_s = true)).text("ascending sort")

      opt[Seq[File]]('i', "input").valueName("<file1>,<file2>...").action((x, c) =>
        c.copy(file_ins = x)).text("input files")

      opt[String]('o', "output").valueName("<new_bib_file>").action((x, c) =>
        c.copy(file_out = x)).text("newly generated bib file")

      cmd("rewrite").action((_, c) => c.copy(mode = "rewrite")).children(
      )

      cmd("check").action((_, c) => c.copy(mode = "check")).children(
        opt[Unit]('d', "duplicates").action((_, c) => c.copy(check_d = true)).text("check duplicates entry"),
        opt[Unit]('f', "fields").action((_, c) => c.copy(check_f = true)).text("check malformed fields")
      )

      cmd("stat").action((_, c) => c.copy(mode = "stat")).children(
        opt[Unit]('s', "short").action((_, c) => c.copy(stat_s = true)).text("short statistics if present")
      )

    }

  }

}
