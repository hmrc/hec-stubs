/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.{JsString, Writes}

sealed trait ReturnStatus extends Product with Serializable {
  def value: String
}

object ReturnStatus {

  case object ReturnFound extends ReturnStatus { override def value = "1" }

  case object NoticeToFileIssued extends ReturnStatus { override def value = "2" }

  case object NoReturnFound extends ReturnStatus { override def value = "3" }

  implicit val writes: Writes[ReturnStatus] = Writes { status =>
    JsString(status.value)
  }

}
