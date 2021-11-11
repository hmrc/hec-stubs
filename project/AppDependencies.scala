import play.core.PlayVersion
import sbt._

object AppDependencies {
  val bootStrapVersion = "5.16.0"
  val compile          = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % bootStrapVersion,
    "org.typelevel" %% "cats-core"                 % "2.6.1"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootStrapVersion    % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.10"            % Test,
    "org.jsoup"               % "jsoup"                  % "1.14.3"            % Test,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.62.2"            % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"             % "test, it"
  )
}
