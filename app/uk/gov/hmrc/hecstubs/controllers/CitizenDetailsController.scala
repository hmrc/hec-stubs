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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.hecstubs.models.citizendetails._
import uk.gov.hmrc.hecstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class CitizenDetailsController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def citizenDetails(nino: String): Action[AnyContent] = Action { _ =>
    val maybeUtr     = if (nino.startsWith("NS")) None else Some("1234567895")
    val responseJson =
      Json.toJson(
        CidPerson(
          Some(CidNames(Some(CidName(Some("Karen"), Some("McKarenFace"))))),
          TaxIds(maybeUtr),
          Some("01121922")
        )
      )

    logger.info(s"Responding to call for citizen details for NINO $nino with JSON: ${responseJson.toString()}")
    Ok(responseJson)
  }
}
