/*
 * Copyright 2021 HM Revenue & Customs
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

sealed trait InvalidCode extends Product with Serializable {
  def value: String
}

object InvalidCode {

  case object InvalidUTR extends InvalidCode { override def value = "INVALID_UTR" }

  case object InvalidCTUTR extends InvalidCode { override def value: String = "INVALID_CTUTR" }

  case object InvalidTaxYear extends InvalidCode { override def value = "INVALID_TAXYEAR" }

  case object InvalidCorrelationId extends InvalidCode { override def value = "INVALID_CORRELATIONID" }

  case object InvalidStartDate extends InvalidCode { override def value = "INVALID_START_DATE" }

  case object InvalidEndDate extends InvalidCode { override def value = "INVALID_END_DATE" }

  case object ServerError extends InvalidCode { override def value = "SERVER_ERROR" }

  implicit val writes: Writes[InvalidCode] = Writes { invalidCode =>
    JsString(invalidCode.value)
  }

}
