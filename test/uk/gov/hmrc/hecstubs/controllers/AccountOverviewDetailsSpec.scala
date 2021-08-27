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

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{status, stubControllerComponents}
import play.api.test.{FakeHeaders, FakeRequest}
import play.api.test.Helpers._

import java.util.UUID
import scala.concurrent.Future
class AccountOverviewDetailsSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def fakeRequest(environment: String, correlationId: String) = FakeRequest(
    method = "GET",
    uri = "/individuals/self-assessment/account-overview/1234567890/2021",
    headers = FakeHeaders(
      Seq(("Content-type", "application/json"), ("Environment", environment), ("CorrelationId", correlationId))
    ),
    body = AnyContentAsEmpty
  )

  private val controller = new IndividualAccountOverviewController(mockCC)

  val validUtr   = "1234567890"
  val inValidUtr = "12345678901"

  val validTaxYear   = "2021"
  val inValidTaxYear = List("202", "20188", "12", "0", "")

  def badJsonResponse(code: String, message: String) = Json.parse(s"""
                                                                    |{
                                                                    |  "failures": [
                                                                    |    {
                                                                    |      "code": "$code",
                                                                    |      "reason": "Submission has not passed validation. $message"
                                                                    |    }
                                                                     |  ]
                                                                    |}
                                                                    |""".stripMargin)

  def badJsonResponseTwo(code1: String, code2: String, message1: String, message2: String) = Json.parse(s"""
                                                                     |{
                                                                     |  "failures": [
                                                                     |    {
                                                                     |      "code": "$code1",
                                                                     |      "reason": "Submission has not passed validation. $message1"
                                                                     |    },
                                                                     |     {
                                                                     |      "code": "$code2",
                                                                     |      "reason": "Submission has not passed validation. $message2"
                                                                     |    }
                                                                     |  ]
                                                                     |}
                                                                     |""".stripMargin)

  def badJsonResponseThree(
    code1: String,
    code2: String,
    code3: String,
    message1: String,
    message2: String,
    message3: String
  ) = Json.parse(s"""
                                      |{
                                      |  "failures": [
                                      |    {
                                      |      "code": "$code1",
                                      |      "reason": "Submission has not passed validation. $message1"
                                      |    },
                                      |     {
                                      |      "code": "$code2",
                                      |      "reason": "Submission has not passed validation. $message2"
                                      |    },
                                      |    {
                                      |      "code": "$code3",
                                      |      "reason": "Submission has not passed validation. $message3"
                                      |    }
                                      |  ]
                                      |}
                                      |""".stripMargin)

  "IndividualAccountOverviewController" when {

    "fetching account overview details " should {

      "return 200 and return the correct response" in {

        val expectedJson: JsValue  = Json.parse("""
            |{
            |    "utr": "1234567890",
            |    "taxYear": "2021",
            |    "returnStatus": "Return Found"
            |}
            |""".stripMargin)
        val result: Future[Result] =
          controller.individualAccountOverview(validUtr, validTaxYear)(fakeRequest("live", UUID.randomUUID().toString))
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "return bad request" when {

        "One invalid parameter" when {

          "utr is Invalid" in {

            val expectedJson: JsValue  = badJsonResponse("INVALID_UTR", "Invalid parameter utr.")
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, validTaxYear)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "taxYear is Invalid" in {

            val expectedJson: JsValue = badJsonResponse("INVALID_TAXYEAR", "Invalid parameter taxYear.")
            inValidTaxYear.map { inTaxYear =>
              val result = controller.individualAccountOverview(validUtr, inTaxYear)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }

          }

          "Wrong Environment value is  passed in the header" in {
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, validTaxYear)(
                fakeRequest("env", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "No Environment value is  passed in the header" in {
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, validTaxYear)(
                fakeRequest("", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "Invalid CorrelationId is  passed in the header" in {
            val expectedJson           = badJsonResponse("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
            val result: Future[Result] =
              controller.individualAccountOverview(validUtr, validTaxYear)(
                fakeRequest("live", "correlationid")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

        }

        "two invalid parameters" when {

          "utr and tax Year are invalid" in {

            val expectedJson: JsValue  =
              badJsonResponseTwo(
                "INVALID_UTR",
                "INVALID_TAXYEAR",
                "Invalid parameter utr.",
                "Invalid parameter taxYear."
              )
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, inValidTaxYear(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "utr and CorrelationId are invalid" in {

            val expectedJson: JsValue  =
              badJsonResponseTwo(
                "INVALID_UTR",
                "INVALID_CORRELATIONID",
                "Invalid parameter utr.",
                "Invalid header CorrelationId."
              )
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, validTaxYear)(
                fakeRequest("live", "correlationId")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "taxYear and CorrelationId are invalid" in {

            val expectedJson: JsValue  =
              badJsonResponseTwo(
                "INVALID_TAXYEAR",
                "INVALID_CORRELATIONID",
                "Invalid parameter taxYear.",
                "Invalid header CorrelationId."
              )
            val result: Future[Result] =
              controller.individualAccountOverview(validUtr, inValidTaxYear(0))(
                fakeRequest("live", "correlationId")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

        }

        "three invalid parameters" when {

          "utr , tax Year and correlationId  are invalid" in {

            val expectedJson: JsValue  =
              badJsonResponseThree(
                "INVALID_UTR",
                "INVALID_TAXYEAR",
                "INVALID_CORRELATIONID",
                "Invalid parameter utr.",
                "Invalid parameter taxYear.",
                "Invalid header CorrelationId."
              )
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr, inValidTaxYear(0))(
                fakeRequest("live", "correlationId")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

        }

      }

    }
  }
}
