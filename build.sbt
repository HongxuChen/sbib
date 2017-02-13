import Dependencies._
import Settings._

lazy val macroSub = (project in file("macro")).
  settings(Settings.commonSettings: _*).
  settings(
    name := "sbib-macro",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value
    )
  )

lazy val sbib = (project in file(".")).
  settings(Settings.commonSettings: _*).
  settings(
    name := "sbib",
    scalacOptions ++= Seq(
      "-P:clippy:colors=true"
    ),
    autoCompilerPlugins := true,
    resolvers ++= sbibResolvers,
    libraryDependencies ++= deps,
    buildInfoKeys := Seq[BuildInfoKey](resourceDirectory, name, version, packageDescription, packageSummary),
    metaMacroSettings
  ).dependsOn(macroSub).settings(mySbtNativeSettings: _*)
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)