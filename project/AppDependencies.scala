import sbt._

object AppDependencies {

  val bootStrapVersion = "8.4.0"
  val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"   %% s"bootstrap-backend-$playVersion" % bootStrapVersion,
    "org.typelevel" %% "cats-core"                       % "2.10.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootStrapVersion % Test
  )
}
