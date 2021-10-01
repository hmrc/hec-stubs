import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-28" % "5.14.0",
    "org.typelevel" %% "cats-core"                 % "2.6.1"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % "5.14.0"            % Test,
    "org.scalatest"          %% "scalatest"              % "3.2.5"             % Test,
    "org.jsoup"               % "jsoup"                  % "1.13.1"            % Test,
    "com.typesafe.play"      %% "play-test"              % PlayVersion.current % Test,
    "com.vladsch.flexmark"    % "flexmark-all"           % "0.36.8"            % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"     % "5.1.0"             % "test, it"
  )
}
