/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.hecstubs.models.accountOverviewDetails

sealed trait Environment extends Product with Serializable {
  def value: String
}
object Environment {
  case object Ist0 extends Environment { override def value: String = "ist0" }
  case object Clone extends Environment { override def value: String = "clone" }
  case object Live extends Environment { override def value: String = "live" }

  def getEnvironmentFromString(env: String): Option[Environment] = env match {
    case "ist0"  => Some(Ist0)
    case "clone" => Some(Clone)
    case "live"  => Some(Live)
    case _       => None
  }

}
