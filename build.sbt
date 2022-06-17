name := """rb-paren-cli"""
organization := "io.github.jisantuc"

ThisBuild / githubOrganization := "io.github.jisantuc"

lazy val Version = new {
  val cats = "2.7.0"
  val catsEffect = "3.3.6"
  val catsScalacheck = "0.3.1"
  val decline = "2.2.0"
  val fs2 = "3.2.8"
  val weaver = "0.7.9"
}

addCommandAlias(
  "ci-test",
  ";scalafmtCheckAll; scalafmtSbtCheck; test"
)
addCommandAlias("ci-publish", "github; ci-release")

val settings =
  Seq(
    organization := "io.github.jisantuc",
    homepage := Some(url("https://github.com/jisantuc/rb-paren-cli")),
    licenses := List(
      "MIT" -> url("https://opensource.org/licenses/MIT")
    ),
    libraryDependencies ++= Seq(
      "co.fs2" %% "fs2-core" % Version.fs2,
      "co.fs2" %% "fs2-io" % Version.fs2,
      "com.disneystreaming" %% "weaver-cats" % Version.weaver % Test,
      "com.disneystreaming" %% "weaver-scalacheck" % Version.weaver % Test,
      "com.monovore" %% "decline-effect" % Version.decline,
      "com.monovore" %% "decline" % Version.decline,
      "io.chrisdavenport" %% "cats-scalacheck" % Version.catsScalacheck % Test,
      "org.typelevel" %% "cats-core" % Version.cats,
      "org.typelevel" %% "cats-effect" % Version.catsEffect
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    run / fork := true
  )

lazy val root = (project in file("."))
  .settings(settings: _*)
