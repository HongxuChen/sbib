package com.github.hongxuchen.sbib.utils

import scala.annotation.tailrec

object Graph {

  def tsort[A](edges: Traversable[(A, A)]): Seq[A] = {
    @tailrec
    def tsort(toPreds: Map[A, Set[A]], done: Seq[A]): Seq[A] = {
      val (noPreds, hasPreds) = toPreds.partition {
        _._2.isEmpty
      }
      if (noPreds.isEmpty) {
        if (hasPreds.isEmpty) done else sys.error(hasPreds.toString)
      } else {
        val found = noPreds.keys
        tsort(hasPreds.mapValues {
          _ -- found
        }, done ++ found)
      }
    }

    val toPred = edges.foldLeft(Map[A, Set[A]]()) { (acc, e) =>
      acc + (e._1 -> acc.getOrElse(e._1, Set())) + (e._2 -> (acc.getOrElse(e._2, Set()) + e._1))
    }
    tsort(toPred, Seq.empty)
  }

}
