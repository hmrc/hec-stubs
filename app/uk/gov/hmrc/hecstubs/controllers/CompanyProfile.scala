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

import play.api.http.Status._
import uk.gov.hmrc.hecstubs.models.{CompanyHouseResponse, GetCTUTRDESResponse}

final case class CompanyProfile(
  crnPredicate: String => Boolean,
  companyHouseResponse: CompanyHouseResponse,
  desResponse: Option[GetCTUTRDESResponse] = None
)

object CompanyProfile {
  import CompanyHouseResponse._
  import GetCTUTRDESResponse._

  val profile1: CompanyProfile = CompanyProfile(
    _.startsWith("1"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(OK, happyDesResponsse))
  )
  val profile2: CompanyProfile = CompanyProfile(
    _.startsWith("21"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(NOT_FOUND, notFoundDeResponse))
  )
  val profile3: CompanyProfile = CompanyProfile(
    _.startsWith("22"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(BAD_REQUEST, badRequestDesResponse))
  )
  val profile4: CompanyProfile = CompanyProfile(
    _.startsWith("23"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(INTERNAL_SERVER_ERROR, serverErrorDesResponse))
  )
  val profile5: CompanyProfile = CompanyProfile(
    _.startsWith("24"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(SERVICE_UNAVAILABLE, serviceUnavailDesResponse))
  )
  val profile6: CompanyProfile = CompanyProfile(
    _.startsWith("3"),
    CompanyHouseResponse(NOT_FOUND, None)
  )
  val profile7: CompanyProfile = CompanyProfile(
    _.startsWith("4"),
    CompanyHouseResponse(INTERNAL_SERVER_ERROR, None)
  )
  val profile8: CompanyProfile = CompanyProfile(
    _.startsWith("5"),
    CompanyHouseResponse(SERVICE_UNAVAILABLE, None)
  )

  private val profiles: List[CompanyProfile] =
    List(profile1, profile2, profile3, profile4, profile5, profile6, profile7, profile8)

  def getProfile(crnReference: String): Option[CompanyProfile] =
    profiles.find(_.crnPredicate(crnReference))

}
