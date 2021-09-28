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
import uk.gov.hmrc.hecstubs.models.GetCTUTRDESResponse.happyDesResponse
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.CTUTR
import uk.gov.hmrc.hecstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class DESController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def getCtutr(crn: String): Action[AnyContent] = Action { _ =>
    val GetCTUTRDESResponse(status, responseBody, _) = CompanyProfile
      .getProfile(crn)
      .flatMap(_.desResponse)
      .getOrElse {
        val ctutr = CTUTR("1111111111")
        GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))
      }
    logger.info(s"Responding to get CTUTR from DES for CRN $crn with status $status & body - $responseBody}")
    Status(status)(responseBody)
  }
}
