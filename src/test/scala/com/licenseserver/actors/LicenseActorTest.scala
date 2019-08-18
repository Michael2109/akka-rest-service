package com.licenseserver.actors

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.licenseserver.actors.LicenseActor.CreateLicense
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, _}
import scala.language.postfixOps

class LicenseActorTest extends WordSpec with Matchers with ScalaFutures {

  "LicenseActor" should {

    implicit lazy val timeout = Timeout(5.seconds)
    implicit val system: ActorSystem = ActorSystem("helloAkkaHttpServer")
    val licenseActor = system.actorOf(LicenseActor.props, "licenseActor")


    "Create a license" in {

      val createdLicense = Await.result((licenseActor ? CreateLicense("Account Name 1", 5)).mapTo[License], Duration.Inf)

      assertResult(createdLicense.userID)("Account Name 1")
      assertResult(createdLicense.activationsLeft)(5)
      assertResult(createdLicense.totalActivations)(5)
      assert(createdLicense.key.nonEmpty)
    }
  }
}
