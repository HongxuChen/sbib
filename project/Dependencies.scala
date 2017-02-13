import sbt._

object Dependencies {
  val monocleVersion = "1.4.0"

  val loggingDeps = Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "ch.qos.logback" % "logback-classic" % "1.1.9"
  )

  val deps = Seq(
    "org.scalameta" %% "scalameta" % "1.4.0",
    "com.github.julien-truffaut" %% "monocle-core" % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-macro" % monocleVersion,
    "com.github.julien-truffaut" %% "monocle-law" % monocleVersion % Test,
    //    "org.scala-graph" %% "graph-core" % "1.11.4",
    "com.beachape" %% "enumeratum" % "1.5.6",
    "org.typelevel" %% "cats-core" % "0.9.0",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "com.github.pathikrit" %% "better-files" % "2.17.1",
    "com.lihaoyi" %% "fastparse" % "0.4.1",
    "com.github.scopt" %% "scopt" % "3.5.0",
    "org.scalatest" %% "scalatest" % "3.2.0-SNAP1" // % Test
  ) ++ loggingDeps

}
