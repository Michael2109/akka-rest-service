package com.licenseserver.database.repositories

import com.licenseserver.actors.License
import com.licenseserver.database.DatabaseConnector
import com.licenseserver.database.tables.LicensesTable
import org.h2.engine.Database
import slick.basic.DatabaseConfig
import slick.dbio.DBIOAction
import slick.jdbc.H2Profile

class LicensesRepository(db: H2Profile#Backend#Database) extends LicensesTable {

  def createTable() = db.run(DBIOAction.seq(licenses.schema.create))

  def dropTable() = db.run(DBIOAction.seq(licenses.schema.drop))

  def insert(license: License) = db.run(licenses += license)


  def getLicenseById(id: Int): Option[License] = {
    db.run((for (license <- licenses if license.id === id) yield license).result.headOption)
  }

  def getLicenseByUserID(accountName: String): Option[License] = {
    db.run((for (license <- licenses if license.userID === accountName) yield license).result.headOption)
  }

  def getLicenseByKey(key: String): Option[License] = {
    db.run((for (license <- licenses if license.key === key) yield license).result.headOption)
  }

  def deleteLicense(id: Int): Unit = {

  }

  def activateLicense(key: String): Boolean = {
    val license = getLicenseByKey(key)

    license match {
      case Some(originalLicense) =>

        if (originalLicense.activationsLeft > 0) {
          val q = for {l <- licenses if l.id === originalLicense.id} yield l.activationsLeft
          db.run(q.update(originalLicense.activationsLeft - 1))
          true
        } else {
          false
        }
      case None => false
    }
  }


  def increaseActivations(licenseId: Int, activationAmount: Int): Boolean = {
    val license = getLicenseById(licenseId)

    license match {
      case Some(originalLicense) =>

        val q = for {l <- licenses if l.id === originalLicense.id} yield (l.activationsLeft, l.totalActivations)
        db.run(q.update((originalLicense.activationsLeft + activationAmount, originalLicense.totalActivations + activationAmount)))

        true
      case None => false
    }
  }
}
