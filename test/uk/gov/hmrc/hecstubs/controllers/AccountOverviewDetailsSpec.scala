package uk.gov.hmrc.hecstubs.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers.status
import play.api.test.{FakeHeaders, FakeRequest, Helpers}

import java.util.UUID
class AccountOverviewDetailsSpec extends AnyWordSpec with Matchers {

  def fakeRequest(environment: String, correlationId: String) = FakeRequest(
    method = "POST",
    uri = "/",
    headers = FakeHeaders(
      Seq(("Content-type", "application/json"), ("Environment", environment), ("CorrelationId", "correlationId"))
    ),
    body = AnyContentAsEmpty
  )

  private val controller = new IndividualAccountOverviewController(Helpers.stubControllerComponents())

  val validUtr   = "1234567890"
  val inValidUtr = "12345678901"

  val validTaxYear   = "2021"
  val inValidTaxYear = List("202", "20188", "1890", "20333")

  "GET /" should {
    "return 200" in {
      val result =
        controller.individualAccountOverview(validUtr, validTaxYear)(fakeRequest("live", UUID.randomUUID().toString))
      status(result)

    }
  }
}
