package com.licenseserver.database.tables

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

final case class License(id: Int, userID: String, key: String, totalUsers: Int)

class LicenseTable(tag: Tag) extends Table[License](tag, "LICENSE") {

  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def userID = column[String]("USER_ID")

  def key = column[String]("KEY")

  def totalUsers = column[Int]("TOTAL_USERS")

  def * = (id, userID, key, totalUsers) <> (License.tupled, License.unapply)
}
