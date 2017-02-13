import com.typesafe.sbt.SbtNativePackager.autoImport.{maintainer, packageDescription, packageSummary}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.autoImport.buildInfoPackage

object Settings {

  lazy val commonSettings = Seq(
    scalaVersion := "2.11.8",
    version := "0.0.1",
    organization := "com.github.hongxuchen",
    buildInfoPackage := organization.value,
    scalacOptions in Test ++= Seq("-Yrangepos")
  )

  lazy val mySbtNativeSettings = Seq(
    maintainer := "Hongxu Chen <hongxuchen1989@gmail.com>",
    packageSummary := name.value,
    packageDescription := "A BibTeX Tool written in Scala"
  )

  lazy val metaMacroSettings: Seq[Def.Setting[_]] = Seq(
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-beta4" cross CrossVersion.full),
    scalacOptions += "-Xplugin-require:macroparadise",
    // temporary workaround for https://github.com/scalameta/paradise/issues/10
    scalacOptions in(Compile, console) := Seq(), // macroparadise plugin doesn't work in repl yet.
    // temporary workaround for https://github.com/scalameta/paradise/issues/55
    sources in(Compile, doc) := Nil // macroparadise doesn't work with scaladoc yet.
  )

  lazy val sbibResolvers = Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayIvyRepo("scalameta", "maven")
  )

}