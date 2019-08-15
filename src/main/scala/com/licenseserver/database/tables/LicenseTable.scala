package com.licenseserver.database.tables

import java.sql.{Date, Timestamp}

import com.licenseserver.database.DatabaseConnector
import org.joda.time.DateTime
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

class LicenseTable(tag: Tag) extends Table[License](tag, "LICENSE") {

  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def userID = column[String]("USER_ID")

  def key = column[String]("KEY")

  def activationsLeft = column[Int]("ACTIVATIONS_LEFT")

  def totalActivations = column[Int]("TOTAL_ACTIVATIONS")

  def * = (id, userID, key, activationsLeft, totalActivations) <> (License.tupled, License.unapply)
}
