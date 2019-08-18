package com.licenseserver.database

import com.licenseserver.actors.License
import com.licenseserver.database.repositories.LicensesRepository
import com.licenseserver.database.tables._
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DatabaseConnector {

  private val db: H2Profile#Backend#Database = Database.forConfig("h2mem1")

  val licensesRepository: LicensesRepository = new LicensesRepository(db)

  licensesRepository.createTable()

  def close(): Unit = {
    db.close()
  }
}
