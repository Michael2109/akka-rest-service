package com.licenseserver.generator

import scala.util.Random

object LicenseGenerator {

  private val LicenseCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"

  def createLicense(userID: String): String ={

    val hashCode = userID.hashCode
    val random = new Random(hashCode)

    val generatedCharacters = (0 until 25).map(_ => LicenseCharacters(random.nextInt(LicenseCharacters.size)))

    generatedCharacters.grouped(5).map(section => section.mkString).mkString("-")
  }

}
