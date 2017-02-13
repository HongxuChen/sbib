package com.github.hongxuchen.sbib.writer

import com.github.hongxuchen.sbib.core.EntryKey
import com.github.hongxuchen.sbib.utils.Utility._
import com.github.hongxuchen.sbib.view._
import com.github.hongxuchen.sbib.view._

class EntryKeyGenerator(entry: Entry) {

  def genKey(d: String = ":"): EntryKey = EntryKey {
    entry match {
      case a: Article => {
        val fafn = a.author.fn
        val year = a.year.last4NonPunctuation
        List(fafn, year).mkString(d)
      }
      case b: Book => {
        val fn = b.authorOrEditor match {
          case Left(author) => author.fn
          case Right(editor) => editor.fn
        }
        val year = b.year.s
        List(fn, year).mkString(d)
      }
      case bl: Booklet => {
        val title = bl.title.s.replaceAll("\\s+", "_")
        val authorFn = bl.author match {
          case Some(a) => a.fn
          case None => {
            reportWarn(s"no authors for booklet ${entry.entryKey}")
          }
        }
        reportWarn("keep unchanged for booklet")
        entry.entryKey.s
      }
      case c: Conference => {
        val author = c.author.fn
        val year = c.year.s
        List(author, year).mkString(d)
      }
      case ib: InBook => {
        val fn = ib.authorOrEditor match {
          case Left(author) => author.fn
          case Right(editor) => editor.fn
        }
        val year = ib.year.s
        List(fn, year).mkString(d)
      }
      case ic: InCollection => {
        val fn = ic.author.fn
        val year = ic.year.s
        List(fn, year).mkString(d)
      }
      case ip: InProceedings => {
        val fn = ip.author.fn
        val year = ip.year.s
        List(fn, year).mkString(d)
      }
      case m: Manual => {
        // TODO ad-hoc
        val fn = m.title.s.replace("\\s+", "_")
        List(m.label, fn).mkString(d)
      }
      case mt: MastersThesis => {
        val fn = mt.author.fn
        List(mt.label, fn).mkString(d)
      }
      case misc: Misc => {
        reportWarn(s"unchanged for ${entry}")
        entry.entryKey.s
      }
      case pt: PhdThesis => {
        val fn = pt.author.fn
        List(pt.label, fn).mkString(d)
      }
      case p: Proceedings => {
        val title = p.title.s.normalized
        val year = p.year.last4NonPunctuation
        val editor = p.editor match {
          case Some(e) => e.fn
          case None => "null"
        }
        List(title, year, editor).mkString(d)
      }
      case tr: TechReport => {
        val fn = tr.author.fn
        List(tr.label, fn).mkString(d)
      }
      case up: UnPublished => {
        val fn = up.author.fn
        List(up.label, fn).mkString(d)
      }
    }
  }

}
