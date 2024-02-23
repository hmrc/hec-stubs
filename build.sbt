import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "hec-stubs"

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;.*config.*;.*BuildInfo.;uk.gov.hmrc.BuildInfo;.*Routes;.*RoutesPrefix*",
    ScoverageKeys.coverageMinimumStmtTotal := 96,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    Test / parallelExecution := false
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.12",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions := Seq(
      "-Ymacro-annotations",
      "-Wconf:src=routes/.*:s", // Silence warnings in generated routes
      "-Wconf:cat=unused-imports&src=html/.*:s", // Silence unused import warnings in twirl templates
      "-Wunused:nowarn"
    ),
    Test / scalacOptions := Seq(
      "-Wconf:cat=value-discard:s"
    ),
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes ++= Seq("org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always),
    Compile / doc / sources := Seq.empty
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(scalafmtOnCompile := true)
  .settings(PlayKeys.playDefaultPort := 10109)
  .settings(scoverageSettings:_*)
