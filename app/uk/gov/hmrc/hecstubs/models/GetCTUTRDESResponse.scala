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

package uk.gov.hmrc.hecstubs.models

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.CTUTR

final case class GetCTUTRDESResponse(status: Int, responseBody: JsValue, ctutr: Option[CTUTR] = None)

object GetCTUTRDESResponse {

  def ctutrRes(ctutr: CTUTR): String =
    s"""
       |{
       |  "CTUTR":  "${ctutr.value}"
       |}
       |""".stripMargin

  val error404Response: String =
    s"""
       |{
       |   "code": "NOT_FOUND",
       |   "reason": "The back end has indicated that CT UTR cannot be returned"
       |}
       |""".stripMargin

  val error400Response: String =
    s"""
       |{
       |   "code": "INVALID_CRN",
       |   "reason": "CRN entered is invalid"
       |}
       |""".stripMargin

  val error500Response: String =
    s"""
       |{
       |   "code": "SERVER_ERROR",
       |   "reason": "Internal Server error"
       |}
       |""".stripMargin

  val error503Response: String =
    s"""
       |{
       |   "code": "SERVICE_UNAVAILABLE",
       |   "reason": "Depending systems are currently not working"
       |}
       |""".stripMargin

  def happyDesResponse(ctutr: CTUTR): JsValue = Json.parse(ctutrRes(ctutr))
  val badRequestDesResponse: JsValue          = Json.parse(error400Response)
  val notFoundDeResponse: JsValue             = Json.parse(error404Response)
  val serverErrorDesResponse: JsValue         = Json.parse(error500Response)
  val serviceUnavailDesResponse: JsValue      = Json.parse(error503Response)

}
