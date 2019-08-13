package com.licenseserver.database.tables

import com.licenseserver.database.DatabaseConnector
import slick.lifted.Tag
import slick.jdbc.H2Profile.api._

final case class RegisteredPC(id: Int, licenseID: Int, pcId: String)

class RegisteredPCsTable(tag: Tag) extends Table[RegisteredPC](tag, "REGISTERED_PCS") {

  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def licenseId = column[Int]("ACCOUNT_NAME")

  def pcId = column[String]("PC_ID")

  def registeredPcFk = foreignKey("REGISTERED_PC_FK", licenseId, DatabaseConnector.licenses)(_.id)

  def * = (id, licenseId, pcId) <> (RegisteredPC.tupled, RegisteredPC.unapply)
}
