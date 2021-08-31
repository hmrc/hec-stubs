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
import uk.gov.hmrc.hecstubs.controllers.Testdata._

import java.util.UUID
import scala.concurrent.Future

class AccountingPeriodControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def fakeRequest(environment: String, correlationId: String) = FakeRequest(
    method = "GET",
    uri = "/",
    headers = FakeHeaders(
      Seq(("Content-type", "application/json"), ("Environment", environment), ("CorrelationId", correlationId))
    ),
    body = AnyContentAsEmpty
  )

  def fakeRequestWithoutCorrelationId(environment: String) = FakeRequest(
    method = "GET",
    uri = "/",
    headers = FakeHeaders(
      Seq(("Content-type", "application/json"), ("Environment", environment))
    ),
    body = AnyContentAsEmpty
  )

  private val controller = new AccountingPeriodController(mockCC)

  val validCtutr   = "1234567890"
  val inValidCtUtr = List("12345678901", "ABC1234567", "AB675^^&hg")

  val validStartDate = "2020-04-05"
  val validEndDate   = "2021-04-05"

  val inValidStartDate = List(
    "2020-04-35",
    "2020-04-",
    "2020- -09",
    " -04-05",
    "2020- - ",
    " - -09",
    " -09- ",
    "220-07-09",
    "1890-07-09",
    "2020-67-09",
    " ",
    ""
  )
  val inValidEndDate   = List(
    "2021-04-35",
    "2021-04-",
    "2021- -09",
    " -04-05",
    "2021- - ",
    " - -09",
    " -09- ",
    "220-07-09",
    "1890-07-09",
    "2021-67-09",
    " ",
    ""
  )

  "AccountingPeriodController" when {
    "fetch accounting period details " should {

      "return 200 and the correct response when all valid values are passed" in {
        val expectedJson: JsValue  = Json.parse("""
                                                  {
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
        val result: Future[Result] = controller.accountingPeriod(validCtutr, "2020-04-05", "2021-04-05")(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "return 200 and the correct response when all valid values are passed except correlation Id" in {
        val expectedJson: JsValue  = Json.parse("""
                                                  {
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
        val result: Future[Result] = controller.accountingPeriod(validCtutr, "2020-04-05", "2021-04-05")(
          fakeRequestWithoutCorrelationId("live")
        )
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "return bad request " when {

        "One invalid parameter" when {

          "ctutr is unvalid" in {

            val expectedJson: JsValue = badJsonResponse("INVALID_CTUTR", "Invalid parameter ctutr.")

            inValidCtUtr.map { invUtr =>
              val result = controller.accountingPeriod(invUtr, validStartDate, validEndDate)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }
          }

          "start Date in query param is Invalid" in {

            val expectedJson: JsValue = badJsonResponse("INVALID_START_DATE", "Invalid query parameter startDate.")
            inValidStartDate.map { inStartDate =>
              val result = controller.accountingPeriod(validCtutr, inStartDate, validEndDate)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }

          }

          "End  Date in query param is Invalid" in {

            val expectedJson: JsValue = badJsonResponse("INVALID_END_DATE", "Invalid query parameter endDate.")
            inValidEndDate.map { inEndDate =>
              val result = controller.accountingPeriod(validCtutr, validStartDate, inEndDate)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
              status(result) shouldBe Status.BAD_REQUEST
              contentAsJson(result) mustBe expectedJson
            }

          }

          "Wrong Environment value is  passed in the header" in {
            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, validStartDate, validEndDate)(
                fakeRequest("env", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST

          }

          "No Environment value is  passed in the header" in {
            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, validStartDate, validEndDate)(
                fakeRequest("", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
          }

          "Invalid CorrelationId is  passed in the header" in {
            val expectedJson           = badJsonResponse("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, validStartDate, validEndDate)(
                fakeRequest("live", "correlationid")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }
        }

        "two invalid parameters" when {

          "ctutr and startDate are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_CTUTR",
                "INVALID_START_DATE",
                "Invalid parameter ctutr.",
                "Invalid query parameter startDate."
              )

            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), validEndDate)(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

          "ctutr and endDate are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_CTUTR",
                "INVALID_END_DATE",
                "Invalid parameter ctutr.",
                "Invalid query parameter endDate."
              )

            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), validStartDate, inValidEndDate(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

          "startDate and endDate are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_START_DATE",
                "INVALID_END_DATE",
                "Invalid query parameter startDate.",
                "Invalid query parameter endDate."
              )

            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, inValidStartDate(0), inValidEndDate(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

          "ctutr and CorrelationId  are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_CTUTR",
                "INVALID_CORRELATIONID",
                "Invalid parameter ctutr.",
                "Invalid header CorrelationId."
              )

            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), validStartDate, validEndDate)(
                fakeRequest("live", "correlationid")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

          "start date and CorrelationId  are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_START_DATE",
                "INVALID_CORRELATIONID",
                "Invalid query parameter startDate.",
                "Invalid header CorrelationId."
              )

            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, inValidStartDate(0), validEndDate)(
                fakeRequest("live", "correlationid")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

          "end date and CorrelationId  are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseTwo(
                "INVALID_END_DATE",
                "INVALID_CORRELATIONID",
                "Invalid query parameter endDate.",
                "Invalid header CorrelationId."
              )

            val result: Future[Result] =
              controller.accountingPeriod(validCtutr, validStartDate, inValidEndDate(0))(
                fakeRequest("live", "correlationid")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

        }

        "three invalid paramaters" when {

          "ctutr, startDate and end date are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseThree(
                "INVALID_CTUTR",
                "INVALID_START_DATE",
                "INVALID_END_DATE",
                "Invalid parameter ctutr.",
                "Invalid query parameter startDate.",
                "Invalid query parameter endDate."
              )

            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), inValidEndDate(0))(
                fakeRequest("live", UUID.randomUUID().toString)
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson

          }

          "ctutr, startDate and correlationId are invalid" in {

            val expectedJson: JsValue =
              badJsonResponseThree(
                "INVALID_CTUTR",
                "INVALID_START_DATE",
                "INVALID_CORRELATIONID",
                "Invalid parameter ctutr.",
                "Invalid query parameter startDate.",
                "Invalid header CorrelationId."
              )

            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), validEndDate)(
                fakeRequest("live", "correlationId")
              )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }
        }

        "four invlid paramters" when {
          "ctutr, startDate, endDate and corelationid are invalid" in {
            val expectedJson: JsValue  =
              badJsonResponseFour(
                "INVALID_CTUTR",
                "INVALID_START_DATE",
                "INVALID_END_DATE",
                "INVALID_CORRELATIONID",
                "Invalid parameter ctutr.",
                "Invalid query parameter startDate.",
                "Invalid query parameter endDate.",
                "Invalid header CorrelationId."
              )
            val result: Future[Result] =
              controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), inValidEndDate(0))(
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
