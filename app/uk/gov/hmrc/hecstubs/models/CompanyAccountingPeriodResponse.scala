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

package uk.gov.hmrc.hecstubs.models

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ReturnStatus
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.{AccountingPeriod, CompanyAccountingPeriod, CompanyAccountingPeriodRequestParameters, ReturnLookupStatus}

final case class CompanyAccountingPeriodResponse(status: Int, responseBody: JsValue)

object CompanyAccountingPeriodResponse {

  def returnFoundResponse(request: CompanyAccountingPeriodRequestParameters): JsValue =
    Json.toJson(
      CompanyAccountingPeriod(
        request.ctutr,
        ReturnLookupStatus.Successful,
        List(
          AccountingPeriod(
            request.startDate,
            request.startDate.plusDays(1L),
            ReturnStatus.NoReturnFound
          ),
          AccountingPeriod(
            request.endDate.minusDays(1L),
            request.endDate,
            ReturnStatus.ReturnFound
          )
        )
      )
    )

  def noReturnFoundResponse(request: CompanyAccountingPeriodRequestParameters): JsValue =
    Json.toJson(
      CompanyAccountingPeriod(
        request.ctutr,
        ReturnLookupStatus.Successful,
        List(
          AccountingPeriod(
            request.startDate,
            request.startDate.plusDays(1L),
            ReturnStatus.ReturnFound
          ),
          AccountingPeriod(
            request.endDate.minusDays(1L),
            request.endDate,
            ReturnStatus.NoReturnFound
          )
        )
      )
    )

  def noticeToFileIssuedResponse(request: CompanyAccountingPeriodRequestParameters): JsValue =
    Json.toJson(
      CompanyAccountingPeriod(
        request.ctutr,
        ReturnLookupStatus.Successful,
        List(
          AccountingPeriod(
            request.startDate,
            request.startDate.plusDays(1L),
            ReturnStatus.ReturnFound
          ),
          AccountingPeriod(
            request.endDate.minusDays(1L),
            request.endDate,
            ReturnStatus.NoticeToFileIssued
          )
        )
      )
    )

  def noAccountingPeriodsResponse(request: CompanyAccountingPeriodRequestParameters): JsValue =
    Json.toJson(
      CompanyAccountingPeriod(
        request.ctutr,
        ReturnLookupStatus.NoLiveRecords,
        List.empty
      )
    )

  val error404Response: JsValue =
    Json.parse(s"""
       |{
       |   "code": "NOT_FOUND",
       |   "reason": " The remote endpoint has indicated that no data can be found for the given identifier."
       |}
       |""".stripMargin)

  val error400Response: JsValue =
    Json.parse(
      s"""
       |{
       |   "code": "INVALID_CTUTR",
       |   "reason": "Submission has not passed validation. Invalid parameter ctutr."
       |}
       |""".stripMargin
    )

  val error422Response: JsValue =
    Json.parse(
      s"""
       |{
       |   "code": "INVALID_DATE",
       |   "reason": "The remote endpoint has indicated that start date is equal to or greater than the end date."
       |}
       |""".stripMargin
    )

  val error500Response: JsValue =
    Json.parse(
      s"""
       |{
       |   "code": "SERVER_ERROR",
       |   "reason": "IF is currently experiencing problems that require live service intervention."
       |}
       |""".stripMargin
    )
  val error502Response: JsValue =
    Json.parse(
      s"""
       |{
       |   "code": "BAD_GATEWAY",
       |   "reason": "Depending systems are currently not responding"
       |}
       |""".stripMargin
    )

  val error503Response: JsValue =
    Json.parse(
      s"""
       |{
       |   "code": "SERVICE_UNAVAILABLE",
       |   "reason": "Depending systems are currently not responding"
       |}
       |""".stripMargin
    )
}