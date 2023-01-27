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
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hecstubs.models.GetCTUTRDESResponse
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.CTUTR

import scala.concurrent.Future

class DESControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  private val fakeRequest = FakeRequest().withMethod("GET")

  private val controller = new DESController(mockCC)

  "DESController" when {

    "fetching CTUTR " should {

      "return CTUTR if CRN starts with 1" in {
        val companyNumber          = "1134567"
        val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe GetCTUTRDESResponse.happyDesResponse(CTUTR("1111111111"))
      }

      "return 404 not found response if CRN starts with 21" in {
        val companyNumber          = "21234567"
        val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
        status(result)        shouldBe Status.NOT_FOUND
        contentAsJson(result) shouldBe GetCTUTRDESResponse.notFoundDeResponse
      }

      "return 400 bad request response if CRN starts with 22" in {
        val companyNumber          = "22234567"
        val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
        status(result)        shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe GetCTUTRDESResponse.badRequestDesResponse
      }

      "return 500 internal server error response if CRN starts with 23" in {
        val companyNumber          = "23234567"
        val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
        status(result)        shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe GetCTUTRDESResponse.serverErrorDesResponse
      }

      "return 503 service unavailable response if CRN starts with 24" in {
        val companyNumber          = "24234567"
        val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
        status(result)        shouldBe Status.SERVICE_UNAVAILABLE
        contentAsJson(result) shouldBe GetCTUTRDESResponse.serviceUnavailDesResponse
      }

      "return CTUTR if CRN starts with anything else" in {
        Seq(
          "1234567" -> CTUTR("2222222222"),
          "1334567" -> CTUTR("3333333333"),
          "1434567" -> CTUTR("4444444444"),
          "2534567" -> CTUTR("1111111111"),
          "4134567" -> CTUTR("9999999999"),
          "4214567" -> CTUTR("9299999998"),
          "4224567" -> CTUTR("9289999996"),
          "4234567" -> CTUTR("9279999994"),
          "4244567" -> CTUTR("9269999992"),
          "4334567" -> CTUTR("9399999995"),
          "4434567" -> CTUTR("9499999992"),
          "4634567" -> CTUTR("9699999997"),
          "4734567" -> CTUTR("9799999994")
        ) foreach { case (companyNumber, expectedCTUTR) =>
          val result: Future[Result] = controller.getCtutr(companyNumber)(fakeRequest)
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe GetCTUTRDESResponse.happyDesResponse(expectedCTUTR)
        }
      }

    }

  }

}
