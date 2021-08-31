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

package uk.gov.hmrc.hecstubs.util

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ErrorResult
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ErrorCode.InvalidCorrelationId

import java.util.UUID
import scala.util.{Failure, Success, Try}

object ValidationUtils {
  def correlationIdValidation(correlationId: String): ValidatedNel[ErrorResult, String] = Try(
    UUID.fromString(correlationId)
  ) match {
    case Success(id) => Valid(id.toString)
    case Failure(_)  =>
      Validated.invalidNel(
        ErrorResult(InvalidCorrelationId, "Submission has not passed validation. Invalid header CorrelationId.")
      )
  }

}
