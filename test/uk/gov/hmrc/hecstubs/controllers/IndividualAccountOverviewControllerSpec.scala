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
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hecstubs.controllers.TestData._
import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.InvalidCode._

import java.util.UUID
import scala.concurrent.Future

class IndividualAccountOverviewControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def fakeRequest(environment: String, correlationId: String) =
    FakeRequest().withMethod("GET").withHeaders(("Environment", environment), ("CorrelationId", correlationId))

  private val controller = new IndividualAccountOverviewController(mockCC)

  val validUtr   = "1234567890"
  val inValidUtr = List("12345678901", "ABC1234567", "AB675^^&hg")

  val validTaxYear   = "2021"
  val inValidTaxYear = List("202", "20188", "12", "0", "")

  "IndividualAccountOverviewController" when {

    "fetching account overview details " should {

      "return 200 and return the correct response" in {

        val result: Future[Result] =
          controller.individualAccountOverview(validUtr, validTaxYear)(fakeRequest("live", UUID.randomUUID().toString))
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedAccountOverviewJson
      }

      "return bad request" when {

        "One invalid parameter" when {

          "utr is Invalid" in {

            val expectedJson: JsValue = badJsonResponse((InvalidUTR, "Invalid parameter utr."))

            inValidUtr.map { invUtr =>
              val result = controller.individualAccountOverview(invUtr, validTaxYear)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }

          }

          "taxYear is Invalid" in {

            val expectedJson: JsValue = badJsonResponse((InvalidTaxYear, "Invalid parameter taxYear."))
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

          "Invalid CorrelationId is  passed in the header" in {
            val expectedJson           = badJsonResponse((InvalidCorrelationId, "Invalid header CorrelationId."))
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
              badJsonResponse((InvalidUTR, "Invalid parameter utr."), (InvalidTaxYear, "Invalid parameter taxYear."))
            val result: Future[Result] =
              controller.individualAccountOverview(inValidUtr(0), inValidTaxYear(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "utr and CorrelationId are invalid" in {

            val expectedJson: JsValue = badJsonResponse(
              (InvalidUTR, "Invalid parameter utr."),
              (InvalidCorrelationId, "Invalid header CorrelationId.")
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
              (InvalidTaxYear, "Invalid parameter taxYear."),
              (InvalidCorrelationId, "Invalid header CorrelationId.")
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
              (InvalidUTR, "Invalid parameter utr."),
              (InvalidTaxYear, "Invalid parameter taxYear."),
              (InvalidCorrelationId, "Invalid header CorrelationId.")
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
