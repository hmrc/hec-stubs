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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.hecstubs.models.CompanyHouseResponse
import uk.gov.hmrc.hecstubs.util.Logging
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.Inject

@Singleton
class CompanyDetailsController @Inject() (cc: ControllerComponents) extends BackendController(cc) with Logging {

  def findCompanyName(companyNumber: String): Action[AnyContent] = Action { _ =>
    val response: Option[CompanyHouseResponse] =
      CompanyProfile.getProfile(companyNumber).map(_.companyHouseResponse)
    response match {
      case Some(companyRes) =>
        (companyRes.status, companyRes.responseBody) match {
          case (OK, Some(jsValue)) =>
            logger.info(
              s"Responding to company house proxy call for company Number : $companyNumber with Json:  ${jsValue.toString()}"
            )
            Ok(jsValue)
          case (status, _)         =>
            logger.info(
              s"Returning status for company house API response  $companyNumber}"
            )
            Status(status)
        }
      case None             =>
        logger.info(
          s"Company name not found in the List  for company number $companyNumber}"
        )
        NotFound
    }

  }
}
