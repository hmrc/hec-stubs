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

import scala.concurrent.Future

class CompanyDetailsControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  private val fakeRequest = FakeRequest().withMethod("GET")

  private val controller = new CompanyDetailsController(mockCC)

  "CompanyDetailsController" when {

    "fetching company details " should {

      "return the company name if company number starts with 1" in {
        val companyNumber         = "1234567"
        val expectedJson: JsValue = Json.parse(s"""
                                                   |{"company_name" : "Test Tech Ltd"}
                                                   |""".stripMargin)

        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "return the company name if company number starts with 2" in {
        val companyNumber         = "21234567"
        val expectedJson: JsValue = Json.parse(s"""
                                                  |{"company_name" : "Test Tech Ltd"}
                                                  |""".stripMargin)

        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "not return company name if company number starts with 3" in {
        val companyNumber          = "31234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }

      "return internal sever error if company number starts with 4" in {
        val companyNumber          = "41234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return service unavailable if company number starts with 5" in {
        val companyNumber          = "51234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

    }

  }

}
