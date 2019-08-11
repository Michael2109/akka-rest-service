package com.akkarestservice.database.tables

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

final case class License(id: Int, customerID: String, key: String)

class LicenseTable(tag: Tag) extends Table[License](tag, "LICENSE") {

  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def customerID = column[String]("CUSTOMER_ID")

  def key = column[String]("KEY")

  def * = (id, customerID, key) <> (License.tupled, License.unapply)
}
