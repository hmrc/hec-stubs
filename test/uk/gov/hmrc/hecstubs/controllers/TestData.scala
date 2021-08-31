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

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.{ErrorResponse, ErrorResult, InvalidCode}

object TestData {

  val expectedAccountingPeriodJson: JsValue = Json.parse("""
                                            |{
                                            |  "ctutr": "1234567890",
                                            |  "returnStatus": "Return Found",
                                            |  "accountingPeriods": [
                                            |    {
                                            |      "accountingPeriod": "01",
                                            |      "accountingPeriodStartDate": "2020-04-05",
                                            |      "accountingPeriodEndDate": "2021-04-05"
                                            |    }
                                            |  ]
                                            |}
                                            |""".stripMargin)

  val expectedAccountOverviewJson: JsValue = Json.parse("""
                                            |{
                                            |    "utr": "1234567890",
                                            |    "taxYear": "2021",
                                            |    "returnStatus": "Return Found"
                                            |}
                                            |""".stripMargin)

  def badJsonResponse(codeWithMessages: (InvalidCode, String)*) = {
    val errorObjects = codeWithMessages.toList
      .map { case (code, reason) =>
        ErrorResult(code, s"Submission has not passed validation. $reason")
      }
    Json.toJson(ErrorResponse(errorObjects))
  }

}
