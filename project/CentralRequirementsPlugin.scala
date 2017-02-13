import sbt.Keys._
import sbt._
import verizon.build.RigPlugin
import xerial.sbt.Sonatype.autoImport.sonatypeProfileName

object CentralRequirementsPlugin extends AutoPlugin {
  // tells sbt to automatically enable this plugin where ever
  // the sbt-rig plugin is enabled (which should be all sub-modules)
  override def trigger = allRequirements

  override def requires = RigPlugin

  override lazy val projectSettings = Seq(
    // this tells sonatype what profile to use
    // (usually this is what you registered when you signed up
    // for maven central release via their OSS JIRA ticket process)
    sonatypeProfileName := "com.github.hongxuchen",
    // inform central who was explicitly involved in developing
    // this project. Note that this is *required* by central.
    developers += Developer("Hongxu", "Hongxu Chen", "", url("http://github.com/HongxuChen")),
    // what license are you releasing this under?
    licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    // where can users find information about this project?
    homepage := Some(url("https://github.com/HongxuChen/sbib")),
    // show users where the source code is located
    scmInfo := Some(ScmInfo(url("https://github.com/HongxuChen/sbib"),
      "git@github.com:HongxuChen/sbib.git"))
  )
}