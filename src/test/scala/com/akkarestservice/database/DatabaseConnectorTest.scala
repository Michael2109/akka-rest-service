package com.akkarestservice.database

import com.akkarestservice.database.tables.License
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class DatabaseConnectorTest extends WordSpec with Matchers with ScalaFutures {

  "DatabaseConnector" should {

    "Add a license to the database" in {
      DatabaseConnector.addLicense(License(0, "Account Name 1", "12345"))

      val foundLicense = DatabaseConnector.getLicenseById(1)
      assert(foundLicense.isDefined)
      assertResult(License(1, "Account Name 1", "12345"))(foundLicense.get)
    }

    "Get a license by ID" in {

      DatabaseConnector.addLicense(License(0, "Account Name 1", "12345"))
      DatabaseConnector.addLicense(License(0, "Account Name 2", "2345"))
      DatabaseConnector.addLicense(License(0, "Account Name 3", "5432"))

      val foundLicense = DatabaseConnector.getLicenseById(2)
      assert(foundLicense.isDefined)
      assertResult(2)(foundLicense.get.id)
    }

    "Get a license by customer id" in {

      DatabaseConnector.addLicense(License(0, "Account Name 1", "12345"))
      DatabaseConnector.addLicense(License(0, "Account Name 2", "2345"))
      DatabaseConnector.addLicense(License(0, "Account Name 3", "5432"))

      val foundLicense = DatabaseConnector.getLicenseByCustomerID("Account Name 2")
      assert(foundLicense.isDefined)
      assertResult("Account Name 2")(foundLicense.get.customerID)
    }
  }
}
