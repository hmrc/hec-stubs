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
import uk.gov.hmrc.hecstubs.models.companyAccountingPeriod.{CTUTR, CompanyAccountingPeriodRequestParameters}
import uk.gov.hmrc.hecstubs.models.{CompanyAccountingPeriodResponse, CompanyHouseResponse, GetCTUTRDESResponse}

final case class CompanyProfile(
  crnPredicate: String => Boolean,
  companyHouseResponse: CompanyHouseResponse,
  desResponse: Option[GetCTUTRDESResponse] = None,
  accountingPeriodsIFResponse: Option[CompanyAccountingPeriodRequestParameters => CompanyAccountingPeriodResponse] =
    None
)

object CompanyProfile {
  import CompanyHouseResponse._
  import GetCTUTRDESResponse._

  val profile1: CompanyProfile = {
    val ctutr = CTUTR("1111111111")
    CompanyProfile(
      _.startsWith("11"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(r => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.returnFoundResponse(r)))
    )
  }

  val profile2: CompanyProfile = {
    val ctutr = CTUTR("2222222222")
    CompanyProfile(
      _.startsWith("12"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(r => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.noticeToFileIssuedResponse(r)))
    )
  }

  val profile3: CompanyProfile = {
    val ctutr = CTUTR("3333333333")
    CompanyProfile(
      _.startsWith("13"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(r => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.noReturnFoundResponse(r)))
    )
  }

  val profile4: CompanyProfile = {
    val ctutr = CTUTR("4444444444")
    CompanyProfile(
      _.startsWith("14"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(r => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.noAccountingPeriodsResponse(r)))
    )
  }

  val profile5: CompanyProfile = CompanyProfile(
    _.startsWith("21"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(NOT_FOUND, notFoundDeResponse))
  )

  val profile6: CompanyProfile = CompanyProfile(
    _.startsWith("22"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(BAD_REQUEST, badRequestDesResponse))
  )

  val profile7: CompanyProfile = CompanyProfile(
    _.startsWith("23"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(INTERNAL_SERVER_ERROR, serverErrorDesResponse))
  )

  val profile8: CompanyProfile = CompanyProfile(
    _.startsWith("24"),
    CompanyHouseResponse(OK, Some(houseResponse)),
    Some(GetCTUTRDESResponse(SERVICE_UNAVAILABLE, serviceUnavailDesResponse))
  )

  val profile9: CompanyProfile = CompanyProfile(
    _.startsWith("31"),
    CompanyHouseResponse(NOT_FOUND, None)
  )

  val profile10: CompanyProfile = CompanyProfile(
    _.startsWith("32"),
    CompanyHouseResponse(INTERNAL_SERVER_ERROR, None)
  )

  val profile11: CompanyProfile = CompanyProfile(
    _.startsWith("33"),
    CompanyHouseResponse(SERVICE_UNAVAILABLE, None)
  )

  val profile12: CompanyProfile = {
    val ctutr = CTUTR("9999999999")
    CompanyProfile(
      _.startsWith("41"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error404Response))
    )
  }

  val profile13: CompanyProfile = {
    val ctutr = CTUTR("9299999998")
    CompanyProfile(
      _.startsWith("42"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error400Response))
    )
  }

  val profile14: CompanyProfile = {
    val ctutr = CTUTR("9399999995")
    CompanyProfile(
      _.startsWith("43"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error422Response))
    )
  }

  val profile15: CompanyProfile = {
    val ctutr = CTUTR("9499999992")
    CompanyProfile(
      _.startsWith("44"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error500Response))
    )
  }

  val profile16: CompanyProfile = {
    val ctutr = CTUTR("9699999997")
    CompanyProfile(
      _.startsWith("46"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error502Response))
    )
  }

  val profile17: CompanyProfile = {
    val ctutr = CTUTR("9799999994")
    CompanyProfile(
      _.startsWith("47"),
      CompanyHouseResponse(OK, Some(houseResponse)),
      Some(GetCTUTRDESResponse(OK, happyDesResponse(ctutr), Some(ctutr))),
      Some(_ => CompanyAccountingPeriodResponse(OK, CompanyAccountingPeriodResponse.error503Response))
    )
  }

  private val profiles: List[CompanyProfile] =
    List(
      profile1,
      profile2,
      profile3,
      profile4,
      profile5,
      profile6,
      profile7,
      profile8,
      profile9,
      profile10,
      profile11,
      profile12,
      profile13,
      profile14,
      profile15,
      profile16,
      profile17
    )

  def getProfile(crnReference: String): Option[CompanyProfile] =
    profiles.find(_.crnPredicate(crnReference))

  def getProfile(ctutr: CTUTR): Option[CompanyProfile] =
    profiles.find(_.desResponse.exists(_.ctutr.contains(ctutr)))

}
