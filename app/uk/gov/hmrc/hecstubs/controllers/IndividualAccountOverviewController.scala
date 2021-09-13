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

package uk.gov.hmrc.hecstubs.controllers

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.syntax.apply._
import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ErrorCode._
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ReturnStatus._
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails._
import uk.gov.hmrc.hecstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.util.UUID
import scala.util.Try

@Singleton
class IndividualAccountOverviewController @Inject() (cc: ControllerComponents)
    extends BackendController(cc)
    with Logging {

  val badRequestMessage = "Submission has not passed validation."
  val utrRegex          = "^[0-9]{10}$"
  val yearRegex         = "^[0-9]{4}$"

  /**
    * Fetch the individual account overview based on utr and tax year.
    * The default status returned is ReturnFound.
    * Provide UTR starting with
    *  - "1111" to receive a status of NoReturnFound
    *  - "2222" to receive a status of NoticeToFileIssued
    * @param utr The individual's UTR
    * @param taxYear Year the tax year ends
    * @return Status for the given tax year
    */
  def individualAccountOverview(utr: String, taxYear: String): Action[AnyContent] = Action { request =>
    val correlationId = request.headers.get("CorrelationId").getOrElse(UUID.randomUUID().toString)
    val environment   = request.headers.get("Environment").flatMap(Environment.getEnvironmentFromString)
    environment match {
      case Some(_) =>
        validateIndividualDetails(utr, taxYear, correlationId) match {
          case Invalid(errorList) =>
            val errorResponse = ErrorResponse(errorList.toList)
            logger.info(
              s"Responding to call for Self-Assessment Individual failed with error Response : ${errorResponse.toString()}"
            )
            BadRequest(Json.toJson(errorResponse))
          case Valid(data)        =>
            val responseJson = Json.toJson(data)
            logger.info(
              s"Responding to call for Self-Assessment Individual Account Overview for UTR $utr and  taxYear: $taxYear with JSON: ${responseJson.toString()}"
            )
            Ok(responseJson)

        }
      case None    =>
        logger.info(
          s"Call failed  for Self-Assessment Individual Account Overview as Environment in header is missing"
        )
        BadRequest
    }

  }

  private val noReturnUtrPrefix           = "1111"
  private val noticeToFileIssuedUtrPrefix = "2222"

  /**
    * validating the utr, year and correlation id
    */
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def validateIndividualDetails(
    utr: String,
    taxYear: String,
    correlationId: String
  ): ValidatedNel[ErrorResult, IndividualAccountOverview] = {

    val utrValidation: ValidatedNel[ErrorResult, SAUTR] = {
      if (utr.matches(utrRegex)) Valid(SAUTR(utr))
      else Validated.invalidNel(ErrorResult(InvalidUTR, s"$badRequestMessage Invalid parameter utr."))
    }

    val taxYearValidation: ValidatedNel[ErrorResult, String] = {
      if (taxYear.matches(yearRegex)) Valid((taxYear.toInt).toString)
      else
        Validated.invalidNel(
          ErrorResult(InvalidTaxYear, s"$badRequestMessage Invalid parameter taxYear.")
        )
    }
    val correlationIdValidation: ValidatedNel[ErrorResult, String] = Try(
      UUID.fromString(correlationId)
    ).toOption match {
      case Some(id) => Valid(id.toString)
      case None     =>
        Validated.invalidNel(
          ErrorResult(InvalidCorrelationId, s"$badRequestMessage Invalid header CorrelationId.")
        )
    }

    val status = if (utr.startsWith(noReturnUtrPrefix)) {
      NoReturnFound
    } else if (utr.startsWith(noticeToFileIssuedUtrPrefix)) {
      NoticeToFileIssued
    } else {
      ReturnFound
    }

    (utrValidation, taxYearValidation, correlationIdValidation)
      .mapN((utr, taxYear, _) => IndividualAccountOverview(utr, taxYear, status))
  }

}
