import sbt.*

object AppDependencies {

  val bootStrapVersion = "10.7.0"
  val playVersion      = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"   %% s"bootstrap-backend-$playVersion" % bootStrapVersion,
    "org.typelevel" %% "cats-core"                       % "2.13.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootStrapVersion % Test
  )
}
