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

import com.google.inject.Singleton
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.hecstubs.models.GetCTUTRDESResponse
import uk.gov.hmrc.hecstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class DESController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  /**
    * Fetch the CTUTR for the given CRN/company number
    * If input CRN starts
    *    - with 1 -> returns 200 success response
    *    - with 21 -> returns 404 not found response
    *    - with 22 -> returns 400 bad request response
    *    - with 23 -> returns 500 internal server error response
    *    - with 24 -> returns 503 service unavailable response
    * @param crn The company number
    * @return HttpResponse
    */
  def getCtutr(crn: String): Action[AnyContent] = Action { _ =>
    val response = CompanyProfile.getProfile(crn).flatMap(_.desResponse)
    response match {
      case Some(desResponse) =>
        (desResponse.status, desResponse.responseBody) match {
          case (OK, responseBody)     =>
            logger.info(s"Responding with success for get CTUTR from DES using CRN $crn with json - $responseBody")
            Ok(responseBody)
          case (status, responseBody) =>
            logger.info(s"Returning status $status for get CTUTR from DES response $crn with json - $responseBody}")
            Status(status)(responseBody)
        }
      case None              =>
        logger.info(s"No profile matched, returning success with JSON ${GetCTUTRDESResponse.ctutrRes}")
        Ok(GetCTUTRDESResponse.happyDesResponse)
    }
  }
}