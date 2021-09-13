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
//import uk.gov.hmrc.hecstubs.models.accountOverviewDetails.ReturnStatus.NoReturnFound

import scala.concurrent.Future

class CitizenDetailsControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  private val fakeRequest = FakeRequest().withMethod("GET")

  private val controller = new CitizenDetailsController(mockCC)

  "CitizenDetailsController" when {

    "fetching citizen details " should {

      "not return the UTR if NINO starts with NOUTR" in {
        val nino                   = "NOUTR67890"
        val expectedJson: JsValue  = Json.parse(s"""
             |{
             |    "name": {
             |      "current": {
             |        "firstName": "Karen",
             |        "lastName": "McKarenFace"
             |      }
             |    },
             |    "ids": {},
             |    "dateOfBirth": "01121922"
             |}
             |""".stripMargin)
        val result: Future[Result] =
          controller.citizenDetails(nino)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }

      "return the UTR if NINO does not start with NOUTR" in {
        val nino                   = "1234567890"
        val expectedJson: JsValue  = Json.parse(s"""
                                                   |{
                                                   |    "name": {
                                                   |      "current": {
                                                   |        "firstName": "Karen",
                                                   |        "lastName": "McKarenFace"
                                                   |      }
                                                   |    },
                                                   |    "ids": {
                                                   |      "sautr": "1234567895"
                                                   |    },
                                                   |    "dateOfBirth": "01121922"
                                                   |}
                                                   |""".stripMargin)
        val result: Future[Result] =
          controller.citizenDetails(nino)(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) mustBe expectedJson
      }
    }
  }
}
