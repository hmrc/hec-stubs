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

      "return the company name" when {

        def test(companyNumber: String) = {
          val expectedJson: JsValue = Json.parse(s"""
               |{"company_name" : "Test Tech Ltd"}
               |""".stripMargin)

          val result: Future[Result] =
            controller.findCompanyName(companyNumber)(fakeRequest)
          status(result) shouldBe Status.OK
          contentAsJson(result) mustBe expectedJson
        }

        "the company number starts with 11" in {
          test("1134567")
        }

        "the company number starts with 12" in {
          test("1234567")
        }

        "the company number starts with 13" in {
          test("1324567")
        }

        "the company number starts with 14" in {
          test("1434567")
        }

        "return the company name if company number starts with 21" in {
          test("21234567")
        }

        "return the company name if company number starts with 22" in {
          test("22234567")
        }

        "return the company name if company number starts with 23" in {
          test("23234567")
        }

        "return the company name if company number starts with 24" in {
          test("24234567")
        }

        "return the company name if company number starts with 41" in {
          test("41234567")
        }

        "return the company name if company number starts with 42" in {
          test("42234567")
        }

        "return the company name if company number starts with 43" in {
          test("43234567")
        }

        "return the company name if company number starts with 44" in {
          test("44234567")
        }

        "return the company name if company number starts with 46" in {
          test("46234567")
        }

        "return the company name if company number starts with 47" in {
          test("47234567")
        }

      }

      "not return company name if company number starts with 31" in {
        val companyNumber          = "31234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.NOT_FOUND
      }

      "return internal sever error if company number starts with 32" in {
        val companyNumber          = "32234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return service unavailable if company number starts with 33" in {
        val companyNumber          = "33234567"
        val result: Future[Result] =
          controller.findCompanyName(companyNumber)(fakeRequest)
        status(result) shouldBe Status.SERVICE_UNAVAILABLE
      }

    }

  }

}
