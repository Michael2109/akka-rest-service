package com.licenseserver.generator

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class LicenseGeneratorTest extends WordSpec with Matchers with ScalaFutures {

  "LicenseGenerator" should {

    "Create a license for `Michael`" in {
      assertResult("S0TF4-EIZUR-Q3KHL-0WV5Z-N9IKS")(LicenseGenerator.createLicense("Michael"))
    }

    "Create a license for `Jack`" in {
      assertResult("I1OK4-VPZT5-M7GV4-JC50T-YEDOM")(LicenseGenerator.createLicense("Jack"))
    }

    "Create a license for `Glen`" in {
      assertResult("EK3YX-UKOC6-256ZA-7TRI5-E98YI")(LicenseGenerator.createLicense("Glen"))
    }

    "Create a license for `Name with spaces`" in {
      assertResult("EK3YX-UKOC6-256ZA-7TRI5-E98YI")(LicenseGenerator.createLicense("Name with spaces"))
    }

    "Create a license for `Name with special characters =-;:'#@~{}][<>,.?`" in {
      assertResult("EK3YX-UKOC6-256ZA-7TRI5-E98YI")(LicenseGenerator.createLicense("Name with special characters =-;:'#@~{}][<>,.?`"))
    }
  }
}
