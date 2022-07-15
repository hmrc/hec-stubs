import play.core.PlayVersion
import sbt._

object AppDependencies {

  val bootStrapVersion = "6.3.0"

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % bootStrapVersion,
    "org.typelevel" %% "cats-core"                 % "2.8.0"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootStrapVersion    % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.12"            % Test,
    "org.jsoup"               % "jsoup"                  % "1.15.2"            % Test,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2"            % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"             % "test, it"
  )
}
