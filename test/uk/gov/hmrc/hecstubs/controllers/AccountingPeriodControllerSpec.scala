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
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hecstubs.controllers.TestData._
import uk.gov.hmrc.hecstubs.models.CompanyAccountingPeriodResponse
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.{CTUTR, CompanyAccountingPeriodRequestParameters}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

class AccountingPeriodControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def fakeRequest(environment: String, correlationId: String) =
    FakeRequest().withMethod("GET").withHeaders(("Environment", environment), ("CorrelationId", correlationId))

  def fakeRequestWithoutCorrelationId(environment: String) =
    FakeRequest().withMethod("GET").withHeaders(("Environment", environment))

  def fakeRequestWithoutEnv() =
    FakeRequest().withMethod("GET")

  private val controller = new AccountingPeriodController(mockCC)

  val validCtutr   = "1234567895"
  val inValidCtUtr = List("12345678901", "ABC1234567", "AB675^^&hg")

  val validStartDateString = "2020-04-05"
  val validEndDateString   = "2021-04-05"

  val validStartDate = LocalDate.parse(validStartDateString)
  val validEndDate   = LocalDate.parse(validEndDateString)

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

    "fetch accounting period details " when {

      "passed all valid values" should {

        def testIsOkWithJson(
          ctutr: CTUTR,
          expectedResponseJson: CompanyAccountingPeriodRequestParameters => JsValue
        ) = {
          val requestParams = CompanyAccountingPeriodRequestParameters(
            ctutr,
            validStartDate,
            validEndDate
          )

          val result: Future[Result] =
            controller.accountingPeriod(requestParams.ctutr.value, validStartDateString, validEndDateString)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe expectedResponseJson(requestParams)
        }

        "return a 'return found' response" when {

          "the CTUTR is 1111111111" in {
            testIsOkWithJson(CTUTR("1111111111"), CompanyAccountingPeriodResponse.returnFoundResponse)
          }

          "the CTUTR is not configured to return other responses" in {
            testIsOkWithJson(CTUTR(validCtutr), CompanyAccountingPeriodResponse.returnFoundResponse)
          }

        }

        "return a 'notice to file issued' response when the CTUTR is 2222222222" in {
          testIsOkWithJson(CTUTR("2222222222"), CompanyAccountingPeriodResponse.noticeToFileIssuedResponse)
        }

        "return a 'no return found' response when the CTUTR is 3333333333" in {
          testIsOkWithJson(CTUTR("3333333333"), CompanyAccountingPeriodResponse.noReturnFoundResponse)
        }

        "return a 'no accounting periods' response when the CTUTR is 4444444444" in {
          testIsOkWithJson(CTUTR("4444444444"), CompanyAccountingPeriodResponse.noAccountingPeriodsResponse)
        }

      }

    }

    "return bad request " when {

      "the CTUTR is 9299999998" in {
        val expectedJson: JsValue = badJsonResponse(("INVALID_CTUTR", "Invalid parameter ctutr."))

        val result = controller.accountingPeriod("9299999998", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) mustBe expectedJson
      }

      "the CTUTR is 9289999996" in {
        val expectedJson: JsValue = badJsonResponse(("INVALID_START_DATE", "Invalid query parameter startDate."))

        val result = controller.accountingPeriod("9289999996", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) mustBe expectedJson
      }

      "the CTUTR is 9279999994" in {
        val expectedJson: JsValue = badJsonResponse(("INVALID_END_DATE", "Invalid query parameter endDate."))

        val result = controller.accountingPeriod("9279999994", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) mustBe expectedJson
      }

      "the CTUTR is 9269999992" in {
        val expectedJson = badJsonResponse(("INVALID_CORRELATIONID", "Invalid header CorrelationId."))

        val result = controller.accountingPeriod("9269999992", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) mustBe expectedJson
      }

      "One invalid parameter" when {

        "ctutr is invalid" in {

          val expectedJson: JsValue = badJsonResponse(("INVALID_CTUTR", "Invalid parameter ctutr."))

          inValidCtUtr.map { invUtr =>
            val result = controller.accountingPeriod(invUtr, validStartDateString, validEndDateString)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }
        }

        "start Date in query param is Invalid" in {

          val expectedJson: JsValue = badJsonResponse(("INVALID_START_DATE", "Invalid query parameter startDate."))

          inValidStartDate.map { inStartDate =>
            val result = controller.accountingPeriod(validCtutr, inStartDate, validEndDateString)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

        }

        "End  Date in query param is Invalid" in {

          val expectedJson: JsValue = badJsonResponse(("INVALID_END_DATE", "Invalid query parameter endDate."))

          inValidEndDate.map { inEndDate =>
            val result = controller.accountingPeriod(validCtutr, validStartDateString, inEndDate)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
            status(result) shouldBe Status.BAD_REQUEST
            contentAsJson(result) mustBe expectedJson
          }

        }

        "Wrong Environment value is  passed in the header" in {
          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, validStartDateString, validEndDateString)(
              fakeRequest("env", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST

        }

        "No Environment value is  passed in the header" in {
          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, validStartDateString, validEndDateString)(
              fakeRequest("", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST
        }

        "Environment header is missing" in {
          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, validStartDateString, validEndDateString)(
              fakeRequestWithoutEnv()
            )
          status(result) shouldBe Status.BAD_REQUEST

        }

        "Invalid CorrelationId is  passed in the header" in {
          val expectedJson           = badJsonResponse(("INVALID_CORRELATIONID", "Invalid header CorrelationId."))
          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, validStartDateString, validEndDateString)(
              fakeRequest("live", "correlationid")
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson

        }
      }

      "two invalid parameters" when {

        "ctutr and startDate are invalid" in {

          val expectedJson: JsValue =
            badJsonResponse(
              ("INVALID_CTUTR", "Invalid parameter ctutr."),
              ("INVALID_START_DATE", "Invalid query parameter startDate.")
            )

          val result: Future[Result] =
            controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), validEndDateString)(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

        "ctutr and endDate are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_CTUTR", "Invalid parameter ctutr."),
            ("INVALID_END_DATE", "Invalid query parameter endDate.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(inValidCtUtr(0), validStartDateString, inValidEndDate(0))(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

        "startDate and endDate are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_START_DATE", "Invalid query parameter startDate."),
            ("INVALID_END_DATE", "Invalid query parameter endDate.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, inValidStartDate(0), inValidEndDate(0))(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

        "ctutr and CorrelationId  are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_CTUTR", "Invalid parameter ctutr."),
            ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(inValidCtUtr(0), validStartDateString, validEndDateString)(
              fakeRequest("live", "correlationid")
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

        "start date and CorrelationId  are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_START_DATE", "Invalid query parameter startDate."),
            ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, inValidStartDate(0), validEndDateString)(
              fakeRequest("live", "correlationid")
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

        "end date and CorrelationId  are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_END_DATE", "Invalid query parameter endDate."),
            ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(validCtutr, validStartDateString, inValidEndDate(0))(
              fakeRequest("live", "correlationid")
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }

      }

      "three invalid parameters" when {

        "ctutr, startDate and end date are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_CTUTR", "Invalid parameter ctutr."),
            ("INVALID_START_DATE", "Invalid query parameter startDate."),
            ("INVALID_END_DATE", "Invalid query parameter endDate.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), inValidEndDate(0))(
              fakeRequest("live", UUID.randomUUID().toString)
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson

        }

        "ctutr, startDate and correlationId are invalid" in {

          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_CTUTR", "Invalid parameter ctutr."),
            ("INVALID_START_DATE", "Invalid query parameter startDate."),
            ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
          )

          val result: Future[Result] =
            controller.accountingPeriod(inValidCtUtr(0), inValidStartDate(0), validEndDateString)(
              fakeRequest("live", "correlationId")
            )
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) mustBe expectedJson
        }
      }

      "four invalid parameters" when {
        "ctutr, startDate, endDate and corelationid are invalid" in {
          val expectedJson: JsValue = badJsonResponse(
            ("INVALID_CTUTR", "Invalid parameter ctutr."),
            ("INVALID_START_DATE", "Invalid query parameter startDate."),
            ("INVALID_END_DATE", "Invalid query parameter endDate."),
            ("INVALID_CORRELATIONID", "Invalid header CorrelationId.")
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

    "return unprocessable entity" when {

      val expectedJson: JsValue = Json.parse(
        """
          |{
          |  "failures" : [
          |    {
          |      "code" : "INVALID_DATE",
          |      "reason" : "The remote endpoint has indicated that start date is equal to or greater than the end date."      
          |    }  
          |  ]
          |}
          |""".stripMargin
      )

      "the CTUTR is 9399999995" in {
        val result = controller.accountingPeriod("9399999995", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.UNPROCESSABLE_ENTITY
        contentAsJson(result) mustBe expectedJson
      }

      "all request parameters can be parsed but the end date is equal to the start date" in {
        val result = controller.accountingPeriod(validCtutr, validStartDateString, validStartDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.UNPROCESSABLE_ENTITY
        contentAsJson(result) mustBe expectedJson
      }

      "all request parameters can be parsed but the end date is before the start date" in {
        val result = controller.accountingPeriod(validCtutr, validEndDateString, validStartDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.UNPROCESSABLE_ENTITY
        contentAsJson(result) mustBe expectedJson
      }
    }

    "return not found" when {

      "the CTUTR is 9999999999" in {
        val result = controller.accountingPeriod("9999999999", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.NOT_FOUND
        contentAsJson(result) mustBe CompanyAccountingPeriodResponse.error404Response
      }

    }

    "return internal server error" when {

      "the CTUTR is 9499999992" in {
        val result = controller.accountingPeriod("9499999992", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) mustBe CompanyAccountingPeriodResponse.error500Response
      }

    }

    "return bad gateway" when {

      "the CTUTR is 9699999997" in {
        val result = controller.accountingPeriod("9699999997", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.BAD_GATEWAY
        contentAsJson(result) mustBe CompanyAccountingPeriodResponse.error502Response
      }

    }

    "return service unavailable" when {

      "the CTUTR is 9799999994" in {
        val result = controller.accountingPeriod("9799999994", validStartDateString, validEndDateString)(
          fakeRequest("live", UUID.randomUUID().toString)
        )
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
        contentAsJson(result) mustBe CompanyAccountingPeriodResponse.error503Response
      }

    }

  }

}
