import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootStrapVersion = "8.4.0"

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % bootStrapVersion,
    "org.typelevel" %% "cats-core"                 % "2.9.0"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootStrapVersion % Test
  )
}
