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

import play.api.libs.json.Json

object Testdata {

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

  def badJsonResponseTwo(code1: String, code2: String, message1: String, message2: String) = Json.parse(
    s"""
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
       |""".stripMargin
  )

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

  def badJsonResponseFour(
    code1: String,
    code2: String,
    code3: String,
    code4: String,
    message1: String,
    message2: String,
    message3: String,
    message4: String
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
                                            |    },
                                            |    {
                                            |      "code": "$code4",
                                            |      "reason": "Submission has not passed validation. $message4"
                                            |    }
                                            |  ]
                                            |}
                                            |""".stripMargin)

}
