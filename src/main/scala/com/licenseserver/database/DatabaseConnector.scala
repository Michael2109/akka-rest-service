package com.licenseserver.database

import java.sql.Timestamp

import com.licenseserver.database.tables._
import org.joda.time.DateTime
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DatabaseConnector {

  private val db = Database.forConfig("h2mem1")

  val licenses = TableQuery[LicenseTable]

  val setup = DBIO.seq(
    (licenses.schema).createIfNotExists
  )

  //  val setupFuture = db.run(setup)
  Await.result(db.run(setup), Duration.Inf)

  def addLicense(license: License): Unit = {
    Await.result(db.run(DBIO.seq(
      licenses += License(license.id, license.userID, license.key, license.activationsLeft, license.totalActivations)
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

  def activateLicense(key: String): Boolean ={
    val license = getLicenseByKey(key)

    license match {
      case Some(originalLicense) =>

        if(originalLicense.activationsLeft > 0) {
          val q = for {l <- licenses if l.id === originalLicense.id} yield l.activationsLeft
          Await.result(db.run(q.update(originalLicense.activationsLeft - 1)), Duration.Inf)
          true
        } else {
          false
        }
      case None => false
    }
  }


  def increaseActivations(licenseId: Int, activationAmount: Int): Boolean ={
    val license = getLicenseById(licenseId)

    license match {
      case Some(originalLicense) =>

        val q = for { l <- licenses if l.id === originalLicense.id } yield (l.activationsLeft, l.totalActivations)
        Await.result(db.run(q.update((originalLicense.activationsLeft + activationAmount, originalLicense.totalActivations + activationAmount))), Duration.Inf)

        true
      case None => false
    }
  }

  def close(): Unit = {
    db.close()
  }
}
