package com.licenseserver

import akka.actor.ActorRef
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.licenseserver.actors.{License, LicenseActor}
import com.licenseserver.routes.Routes
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}


class RoutesTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with Routes {

  override def licenseActor: ActorRef = system.actorOf(LicenseActor.props, "userRegistry")

  "/api/licenses" should {
    "return no licenses" in {
      // note that there's no need for the host part in the uri:
      val request = getLicensesRequest()

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"licenses":[]}""")
      }
    }

    "return one license" in {



      // note that there's no need for the host part in the uri:
      val request = getLicensesRequest()

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"licenses":[]}""")
      }
    }
  }

  private def getLicensesRequest(): HttpRequest = HttpRequest(uri = "/api/licenses")

  private def addLicenseRequest(): HttpRequest = HttpRequest(uri = "/api/licenses/id/")

  private  def addLicense(license: License): Unit ={

  }

}
