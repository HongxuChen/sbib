addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")

addSbtPlugin("com.fortysevendeg" % "sbt-microsites" % "0.3.3")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("com.softwaremill.clippy" % "plugin-sbt" % "0.4.1")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0") // fot sbt-0.13.5 or higher

//addSbtPlugin("com.thoughtworks.sbt-best-practice" % "sbt-best-practice" % "2.1.0")

addSbtPlugin("io.verizon.build" % "sbt-rig" % "1.3.27")

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)