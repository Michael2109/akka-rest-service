package com.licenseserver.database.repositories

import com.licenseserver.actors.License
import com.licenseserver.database.tables.LicensesTable
import slick.dbio.{DBIOAction, Effect}
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._
import slick.sql.FixedSqlAction

import scala.concurrent.Future


class LicensesRepository(db: H2Profile#Backend#Database) extends LicensesTable {

  def createTable(): Future[Unit] = db.run(DBIOAction.seq(licenses.schema.create))

  def dropTable(): Future[Unit] = db.run(DBIOAction.seq(licenses.schema.drop))

  def insert(license: License): Future[Int] = db.run(licenses += license)


  def getLicenseById(id: Int): Future[Option[License]] = {
    db.run((for (license <- licenses if license.id === id) yield license).result.headOption)
  }

  def getLicenseByUserID(accountName: String): Future[Option[License]] = {
    db.run((for (license <- licenses if license.userID === accountName) yield license).result.headOption)
  }

  def getLicenseByKey(key: String): Future[Option[License]] = {
    db.run((for (license <- licenses if license.key === key) yield license).result.headOption)
  }

  def deleteLicense(id: Int): Future[Int] = {
    db.run(licenses.filter(_.id === id).delete)
  }

  def updateByID(license: License): Future[Int] = {
    db.run(licenses.insertOrUpdate(license))
  }

  def updateActivationsLeftByID(id: Int, activationsLeft: Int): Future[Int] ={
    val q = for { l <- licenses if l.id === id } yield l.activationsLeft
    db.run(q.update(activationsLeft))
  }


  def updateTotalActivations(id: Int, totalActivations: Int): Future[Int] = {
    val q = for { l <- licenses if l.id === id } yield l.totalActivations
    db.run(q.update(totalActivations))
  }
}
