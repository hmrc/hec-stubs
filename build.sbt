import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import wartremover.Wart
import wartremover.WartRemover.autoImport.wartremoverErrors

val appName = "hec-stubs"

val silencerVersion = "1.7.8"

lazy val wartremoverSettings =
  Seq(
    (Compile / compile / wartremoverErrors) ++= Warts.allBut(
      Wart.DefaultArguments,
      Wart.ImplicitConversion,
      Wart.ImplicitParameter,
      Wart.Nothing,
      Wart.Overloading,
      Wart.ToString
    ),
    wartremover.WartRemover.autoImport.wartremoverExcluded ++=
      (Compile / routes).value ++
        (baseDirectory.value ** "*.sc").get ++
        Seq(sourceManaged.value / "main" / "sbt-buildinfo" / "BuildInfo.scala"),
    (Test / compile / wartremoverErrors) --= Seq(Wart.Any, Wart.NonUnitStatements, Wart.Null, Wart.PublicInference)
  )

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.15",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions ++= Seq("-P:silencer:pathFilters=routes"),
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    ),
    // ***************
    Compile / doc / sources := Seq.empty
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(wartremoverSettings: _*)
  .settings(scalafmtOnCompile := true)
  .settings(PlayKeys.playDefaultPort := 10109)
