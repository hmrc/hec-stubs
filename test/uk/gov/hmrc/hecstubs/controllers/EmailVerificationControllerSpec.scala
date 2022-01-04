/*
 * Copyright 2022 HM Revenue & Customs
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

import akka.stream.testkit.NoMaterializer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsDefined, JsObject, JsString, JsValue, Json}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class EmailVerificationControllerSpec extends AnyWordSpec with Matchers {

  lazy val mockCC = stubControllerComponents()

  def requestWithJsonBody(json: JsValue) =
    FakeRequest().withBody(json).withHeaders(CONTENT_TYPE -> JSON)

  val controller                         = new EmailVerificationController(mockCC)

  def checkErrorCode(result: Future[Result], expectedErrorCode: String) =
    (contentAsJson(result).as[JsObject] \ "code") shouldBe JsDefined(JsString(expectedErrorCode))

  "EmailVerificationController" when {

    "handling requests to request a passcode" must {

      def performActionWithJsonBody(json: JsValue) =
        controller.requestPasscode(requestWithJsonBody(json))

      def validJsonBody(email: String) = Json.parse(
        s"""{ "email": "$email", "serviceName": "service", "lang": "en" }""".stripMargin
      )

      "return a 415 (unsupported media type)" when {

        "there is no JSON in the request body" in {
          val result = controller.requestPasscode(FakeRequest()).run()(NoMaterializer)

          status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
        }

      }

      "return a 400 (bad request)" when {

        "the json body cannot be parsed" in {
          val result = performActionWithJsonBody(JsString("hi"))

          status(result) shouldBe BAD_REQUEST
          checkErrorCode(result, "VALIDATION_ERROR")
        }

        "the email is 'bad_email_request@email.com'" in {
          val result = performActionWithJsonBody(validJsonBody("bad_email_request@email.com"))

          status(result) shouldBe BAD_REQUEST
          checkErrorCode(result, "BAD_EMAIL_REQUEST")
        }

      }

      "return a 401 (unauthorised)" when {

        "the email is 'no_session_id@email.com'" in {
          val result = performActionWithJsonBody(validJsonBody("no_session_id@email.com"))

          status(result) shouldBe UNAUTHORIZED
          checkErrorCode(result, "NO_SESSION_ID")
        }

      }

      "return a 409 (conflict)" when {

        "the email is 'email_verified_already@email.com'" in {
          val result = performActionWithJsonBody(validJsonBody("email_verified_already@email.com"))

          status(result) shouldBe CONFLICT
          checkErrorCode(result, "EMAIL_VERIFIED_ALREADY")
        }

      }

      "return a 403 (forbidden)" when {

        "the email is 'max_emails_exceeded@email.com'" in {
          val result = performActionWithJsonBody(validJsonBody("max_emails_exceeded@email.com"))

          status(result) shouldBe FORBIDDEN
          checkErrorCode(result, "MAX_EMAILS_EXCEEDED")
        }

      }

      "return a 502 (bad gateway)" when {
        "the email is 'upstream_error@email.com'" in {
          val result = performActionWithJsonBody(validJsonBody("upstream_error@email.com"))

          status(result) shouldBe BAD_GATEWAY
          checkErrorCode(result, "UPSTREAM_ERROR")
        }
      }

      "return a 201 (created) otherwise" in {
        val result = performActionWithJsonBody(validJsonBody("test@email.com"))
        status(result) shouldBe CREATED

      }

    }

    "handling requests to verify a passcode" must {

      def performActionWithJsonBody(json: JsValue) =
        controller.verifyPasscode(requestWithJsonBody(json))

      def validJsonBody(passcode: String) = Json.parse(
        s"""{ "passcode": "$passcode", "email": "email" }""".stripMargin
      )

      "return a 415 (unsupported media type)" when {

        "there is no JSON in the request body" in {
          val result = controller.verifyPasscode(FakeRequest()).run()(NoMaterializer)

          status(result) shouldBe UNSUPPORTED_MEDIA_TYPE
        }

      }

      "return a 400 (bad request)" when {

        "the json body cannot be parsed" in {
          val result = performActionWithJsonBody(JsString("hi"))

          status(result) shouldBe BAD_REQUEST
          checkErrorCode(result, "VALIDATION_ERROR")
        }

      }

      "return a 401 (unauthorised)" when {

        "the passcode is 'BBBBBB'" in {
          val result = performActionWithJsonBody(validJsonBody("BBBBBB"))

          status(result) shouldBe UNAUTHORIZED
          checkErrorCode(result, "NO_SESSION_ID")
        }

      }

      "return a 403 (forbidden)" when {

        "the passcode is 'CCCCCC'" in {
          val result = performActionWithJsonBody(validJsonBody("CCCCCC"))

          status(result) shouldBe FORBIDDEN
          checkErrorCode(result, "MAX_PASSCODE_ATTEMPTS_EXCEEDED")
        }

      }

      "return a 404 (not found)" when {

        "the passcode is 'DDDDDD'" in {
          val result = performActionWithJsonBody(validJsonBody("DDDDDD"))

          status(result) shouldBe NOT_FOUND
          checkErrorCode(result, "PASSCODE_NOT_FOUND")
        }

        "the passcode is 'FFFFFF'" in {
          val result = performActionWithJsonBody(validJsonBody("FFFFFF"))

          status(result) shouldBe NOT_FOUND
          checkErrorCode(result, "PASSCODE_MISMATCH")
        }

      }

      "return a 204 (no content)" when {

        "the passcode is 'GGGGGG'" in {
          val result = performActionWithJsonBody(validJsonBody("GGGGGG"))

          status(result) shouldBe NO_CONTENT
        }

      }

      "return a 201 (created) otherwise" in {
        val result = performActionWithJsonBody(validJsonBody("BCDFGH"))
        status(result) shouldBe CREATED

      }

    }

  }

}
