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

package uk.gov.hmrc.hecstubs.controllers

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.syntax.apply._
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.hecstubs.models.CompanyAccountingPeriodResponse
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ErrorCode._
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.{Environment, ErrorResponse, ErrorResult}
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.{CTUTR, CompanyAccountingPeriodRequestParameters}
import uk.gov.hmrc.hecstubs.util.{Logging, ValidationUtils}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import java.util.UUID

@Singleton
class AccountingPeriodController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  val badRequestMessage                                = "Submission has not passed validation."
  val ctutrRegex                                       = "^[0-9]{10}$"
  val dateRegex                                        =
    "^(((19|20)([2468][048]|[13579][26]|0[48])|2000)[-]02[-]29|((19|20)[0-9]{2}[-](0[469]|11)[-](0[1-9]|1[0-9]|2[0-9]|30)|(19|20)[0-9]{2}[-](0[13578]|1[02])[-](0[1-9]|[12][0-9]|3[01])|(19|20)[0-9]{2}[-]02[-](0[1-9]|1[0-9]|2[0-8])))$"
  val incompatibleStartAndEndDateResponseJson: JsValue =
    Json.toJson(
      ErrorResponse(
        List(
          ErrorResult(
            InvalidDate,
            "The remote endpoint has indicated that start date is equal to or greater than the end date."
          )
        )
      )
    )

  def accountingPeriod(ctutr: String, startDate: String, endDate: String): Action[AnyContent] = Action { request =>
    val correlationId = request.headers.get("CorrelationId").getOrElse(UUID.randomUUID().toString)
    val environment   = request.headers.get("Environment").flatMap(Environment.getEnvironmentFromString)
    environment match {
      case Some(_) => validatedDataResponse(ctutr, startDate, endDate, correlationId)
      case None    =>
        logger.info(
          s"Call failed to fetch  Company Accounting Period details as Environment in header is invalid"
        )
        BadRequest
    }
  }

  private def validatedDataResponse(ctutr: String, startDate: String, endDate: String, correlationId: String) =
    validateInputData(ctutr, startDate, endDate, correlationId) match {
      case Invalid(errorList) =>
        val errorResponse = ErrorResponse(errorList.toList)
        logger.info(
          s"Responding to call for Company Accounting Period failed with error Responses : ${errorResponse.toString()}"
        )
        BadRequest(Json.toJson(errorResponse))

      case Valid(params @ CompanyAccountingPeriodRequestParameters(ctutr, startDate, endDate)) =>
        if (startDate.isBefore(endDate)) {
          val (status, responseBody) =
            CompanyProfile.getProfile(ctutr).flatMap(_.accountingPeriodsIFResponse.map(_(params))) match {
              case Some(CompanyAccountingPeriodResponse(status, responseBody)) =>
                status -> responseBody

              case None =>
                OK -> CompanyAccountingPeriodResponse.returnFoundResponse(params)
            }
          logger.info(
            s"Responding to call for Company Accounting Period  for UTR $ctutr , startDate: $startDate and " +
              s"endDate: $endDate with status $status and JSON: ${responseBody.toString()}"
          )
          Status(status)(responseBody)
        } else
          UnprocessableEntity(incompatibleStartAndEndDateResponseJson)
    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def validateInputData(
    ctutr: String,
    startDate: String,
    endDate: String,
    correlationId: String
  ): ValidatedNel[ErrorResult, CompanyAccountingPeriodRequestParameters] = {

    val ctutrValidation: ValidatedNel[ErrorResult, CTUTR] =
      if (ctutr.matches(ctutrRegex)) Valid(CTUTR(ctutr))
      else
        Validated.invalidNel(
          ErrorResult(InvalidCTUTR, s"$badRequestMessage Invalid parameter ctutr.")
        )

    val startDateValidation: ValidatedNel[ErrorResult, LocalDate] =
      if (startDate.matches(dateRegex)) Valid(LocalDate.parse(startDate))
      else
        Validated.invalidNel(
          ErrorResult(InvalidStartDate, s"$badRequestMessage Invalid query parameter startDate.")
        )

    val endDateValidation: ValidatedNel[ErrorResult, LocalDate] =
      if (endDate.matches(dateRegex)) Valid(LocalDate.parse(endDate))
      else
        Validated.invalidNel(
          ErrorResult(InvalidEndDate, s"$badRequestMessage Invalid query parameter endDate.")
        )

    val correlationIdValidation: ValidatedNel[ErrorResult, String] =
      ValidationUtils.correlationIdValidation(correlationId)

    (ctutrValidation, startDateValidation, endDateValidation, correlationIdValidation).mapN(
      (ctutr, startDate, endDate, _) => CompanyAccountingPeriodRequestParameters(ctutr, startDate, endDate)
    )

  }
}
