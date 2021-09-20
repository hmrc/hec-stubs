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

  /**
    * The rules to process company number is as follows:
    * -If company number starts with 1, it gives ok response from company proxy api and CTUTR api.
    * -If company number starts with 21, it gives ok response from company proxy api but not found from  CTUTR api.
    * -If company number starts with 22, it gives ok response from company proxy api and BAD_REQUEST from CTUTR api.
    * -If company number starts with 23, it gives ok response from company proxy api and INTERNAL_SERVER_ERROR from CTUTR api.
    * -If company number starts with 24, it gives ok response from company proxy api and SERVICE_UNAVAILABLE from CTUTR api.
    * -If company number starts with 3, it gives NOT_FOUND response from company proxy api.
    * -If company number starts with 4, it gives INTERNAL_SERVER_ERROR response from company proxy api.
    * -If company number starts with 5, it gives SERVICE_UNAVAILABLE response from company proxy api.
    * @param companyNumber
    * @return HttpResponse
    */
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

          case _ => InternalServerError
        }
      case None             =>
        logger.info(
          s"Company name not found in the List  for company number $companyNumber}"
        )
        NotFound
    }

  }
}
