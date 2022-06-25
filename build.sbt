name := """rb-paren-cli"""
organization := "io.github.jisantuc"

ThisBuild / githubOrganization := "io.github.jisantuc"

lazy val Version = new {
  val cats = "2.7.0"
  val catsEffect = "3.3.6"
  val catsParse = "0.3.7"
  val decline = "2.2.0"
  val fs2 = "3.2.8"
  val munit = "0.7.29"
}

addCommandAlias(
  "ci-test",
  ";scalafmtCheckAll; scalafmtSbtCheck; test"
)

val settings =
  Seq(
    organization := "io.github.jisantuc",
    homepage := Some(url("https://github.com/jisantuc/rb-paren-cli")),
    licenses := List(
      "MIT" -> url("https://opensource.org/licenses/MIT")
    ),
    libraryDependencies ++= Seq(
      "co.fs2" %%% "fs2-core" % Version.fs2,
      "co.fs2" %%% "fs2-io" % Version.fs2,
      "com.monovore" %%% "decline-effect" % Version.decline,
      "com.monovore" %%% "decline" % Version.decline,
      "org.scalameta" %%% "munit-scalacheck" % Version.munit % Test,
      "org.scalameta" %%% "munit" % Version.munit % Test,
      "org.typelevel" %%% "cats-core" % Version.cats,
      "org.typelevel" %%% "cats-effect" % Version.catsEffect,
      "org.typelevel" %%% "cats-parse" % Version.catsParse
    ),
    run / fork := true,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

lazy val root =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("."))
    .settings(settings: _*)

lazy val rootJVM = root.jvm
  .settings(
    graalVMNativeImageOptions ++= Seq(
      // prevents image creation and prints more output when bundling
      // is *waves hands* incomplete
      "--no-fallback",
      // creates basically a fat jar instead of a binary that requires
      // path / classpath information to execute
      "--static",
      // provides nicer logging output when exceptions are encountered
      "-H:+ReportExceptionStackTraces"
    ),
    GraalVMNativeImage / name := "rainbow-parens"
  )
  .enablePlugins(GraalVMNativeImagePlugin)
