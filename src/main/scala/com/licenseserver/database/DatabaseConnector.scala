package com.licenseserver.database

import com.licenseserver.database.tables._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DatabaseConnector {

  private val db = Database.forConfig("h2mem1")

  val licenses = TableQuery[LicenseTable]

  val registeredPcs = TableQuery[RegisteredPCsTable]

  val setup = DBIO.seq(
    (licenses.schema ++ registeredPcs.schema).createIfNotExists
  )

  //  val setupFuture = db.run(setup)
  Await.result(db.run(setup), Duration.Inf)

  def addLicense(license: License): Unit = {
    //  val setupFuture = db.run(setup)
    Await.result(db.run(DBIO.seq(
      licenses += License(license.id, license.userID, license.key, license.totalUsers)
    )), Duration.Inf)
  }

  def getLicenses(): Licenses = {
    Licenses(Await.result(db.run(licenses.result), Duration.Inf))
  }

  def getLicenseById(id: Int): Option[License] = {
    Await.result(db.run((for (license <- licenses if license.id === id) yield license).result.headOption), Duration.Inf)
  }

  def getLicenseByCustomerID(accountName: String): Option[License] = {
    Await.result(db.run((for (license <- licenses if license.userID === accountName) yield license).result.headOption), Duration.Inf)
  }

  def getLicenseByKey(key: String): Option[License] = {
    Await.result(db.run((for (license <- licenses if license.key === key) yield license).result.headOption), Duration.Inf)
  }

  def addRegisteredPC(registeredPC: RegisteredPC): Unit = {

    //  val setupFuture = db.run(setup)
    Await.result(db.run(DBIO.seq(
      registeredPcs += RegisteredPC(registeredPC.id, registeredPC.licenseID, registeredPC.pcId)
    )), Duration.Inf)
  }

  def getRegisteredPCsByLicenseID(licenseId: Int): Option[RegisteredPC] = {
    Await.result(db.run((for (registeredPC <- registeredPcs if registeredPC.licenseId === licenseId) yield registeredPC).result.headOption), Duration.Inf)
  }


  def close(): Unit = {
    db.close()
  }
}
