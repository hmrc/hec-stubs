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

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hecstubs.controllers.TestData._
import java.util.UUID

import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ReturnStatus.{NoReturnFound, NoticeToFileIssued, ReturnFound}

import scala.concurrent.Future

class IndividualAccountOverviewControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def fakeRequest(environment: String, correlationId: String) =
    FakeRequest().withMethod("GET").withHeaders(("Environment", environment), ("CorrelationId", correlationId))

  def fakeRequestWithoutEnv(correlationId: String) =
    FakeRequest().withMethod("GET").withHeaders(("CorrelationId", correlationId))

  private val controller = new IndividualAccountOverviewController(mockCC)

  val validUtr   = "1234567890"
  val inValidUtr = List("12345678901", "ABC1234567", "AB675^^&hg", "111156", "222234")

  val validTaxYear   = "2021"
  val inValidTaxYear = List("202", "20188", "12", "0", "")

  "IndividualAccountOverviewController" when {

    "fetching account overview details " should {

      "return 200 with correct status" when {
        "utr starts with 1111" in {
          val utr                    = "1111567890"
          val expectedJson: JsValue  = Json.parse(s"""
               |{
               |    "utr": "$utr",
               |    "taxYear": "2021",
               |    "returnStatus": "${NoReturnFound.value}"
               |}
               |""".stripMargin)
          val result: Future[Result] =
            controller.individualAccountOverview(utr, validTaxYear)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe expectedJson
        }

        "utr starts with 2222" in {
          val utr                    = "2222567890"
          val expectedJson: JsValue  = Json.parse(s"""
               |{
                   "utr": "$utr",
               |    "taxYear": "2021",
               |    "returnStatus": "${NoticeToFileIssued.value}"
               |}
               |""".stripMargin)
          val result: Future[Result] =
            controller.individualAccountOverview(utr, validTaxYear)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe expectedJson
        }

        "utr ends with 1231 return NOT_FOUND" in {
          val utr                    = "1233211231"
          val result: Future[Result] =
            controller.individualAccountOverview(utr, validTaxYear)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.NOT_FOUND
        }

        "utr starts with any other string" in {
          val utr                    = "1234567890"
          val expectedJson: JsValue  = Json.parse(s"""
               |{
                   "utr": "$utr",
               |    "taxYear": "2021",
               |    "returnStatus": "${ReturnFound.value}"
               |}
               |""".stripMargin)
          val result: Future[Result] =
            controller.individualAccountOverview(utr, validTaxYear)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe expectedJson
        }
      }

      "return internal server error" when {
        "SAUTR starts with 3333" in {
          val utr                    = "3333333333"
          val result: Future[Result] =
            controller.individualAccountOverview(utr, validTaxYear)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "return bad request" when {

        "One invalid parameter" when {

          "utr is Invalid" in {

            val expectedJson: JsValue = badJsonResponse(("INVALID_UTR", "Invalid parameter utr."))

            inValidUtr.map { invUtr =>
              val result = controller.individualAccountOverview(invUtr, validTaxYear)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }

          }

          "taxYear is Invalid" in {

            val expectedJson: JsValue = badJsonResponse(("INVALID_TAXYEAR", "Invalid parameter taxYear."))
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
              controller.individualAccountOverview(validUtr, validTaxYear)(
                fakeRequest("env", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "No Environment value is  passed in the header" in {
            val result: Future[Result] =
              controller.individualAccountOverview(validUtr, validTaxYear)(
                fakeRequest("", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "Environment header is missing" in {
            val result: Future[Result] =
              controller.individualAccountOverview(validUtr, validTaxYear)(
                fakeRequestWithoutEnv(UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "Invalid CorrelationId is  passed in the header" in {
            val expectedJson           = badJsonResponse(("INVALID_CORRELATIONID", "Invalid header CorrelationId."))
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
              badJsonResponse(
                ("INVALID_UTR", "Invalid parameter utr."),
                ("INVALID_TAXYEAR", "Invalid parameter taxYear.")
              )
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr(0), inValidTaxYear(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "utr and CorrelationId are invalid" in {

            val expectedJson: JsValue = badJsonResponse(
              ("INVALID_UTR", "Invalid parameter utr."),
              ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
            )

            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr(0), validTaxYear)(
                fakeRequest("live", "correlationId")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "taxYear and CorrelationId are invalid" in {

            val expectedJson: JsValue = badJsonResponse(
              ("INVALID_TAXYEAR", "Invalid parameter taxYear."),
              ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
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

            val expectedJson: JsValue = badJsonResponse(
              ("INVALID_UTR", "Invalid parameter utr."),
              ("INVALID_TAXYEAR", "Invalid parameter taxYear."),
              ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
            )

            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr(0), inValidTaxYear(0))(
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
