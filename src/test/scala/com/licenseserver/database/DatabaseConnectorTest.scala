package com.licenseserver.database

import com.licenseserver.Server
import com.licenseserver.actors.License
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class DatabaseConnectorTest extends WordSpec with Matchers with ScalaFutures {

  "DatabaseConnector" should {

    "Add a license to the database" in {
      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 1", "12345", 1, 1))

      val foundLicense = DatabaseConnector.licensesRepository.getLicenseById(1)
      assert(foundLicense.isDefined)
      assertResult(License(1, "Account Name 1", "12345", 1, 1))(foundLicense.get)
    }

    "Get a license by ID" in {

      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 1", "12345", 1, 1))
      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 2", "2345", 1, 1))
      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 3", "5432", 1, 1))

      val foundLicense = DatabaseConnector.licensesRepository.getLicenseById(2)
      assert(foundLicense.isDefined)
      assertResult(2)(foundLicense.get.id)
    }

    "Get a license by customer id" in {

      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 1", "12345", 1, 1))
      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 2", "2345", 1, 1))
      DatabaseConnector.licensesRepository.insert(License(0, "Account Name 3", "5432", 1, 1))

      val foundLicense = DatabaseConnector.licensesRepository.getLicenseByUserID("Account Name 2")
      assert(foundLicense.isDefined)
      assertResult("Account Name 2")(foundLicense.get.userID)
    }
  }
}
