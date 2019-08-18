package com.licenseserver.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.licenseserver.Server
import com.licenseserver.actors.LicenseActor._
import com.licenseserver.database.DatabaseConnector
import com.licenseserver.generator.LicenseGenerator

final case class License(id: Int, userID: String, key: String, activationsLeft: Int, totalActivations: Int)

object LicenseActor {

  final case class ActivateLicense(key: String)

  final case class CreateLicense(userID: String, activationTotal: Int)

  final case class GetLicenseByID(id: Int)

  final case class GetLicenseByKey(key: String)

  final case class GetLicenseByUser(userID: String)

  final case object GetLicenses

  final case class DeleteLicense(id: Int)

  def props: Props = Props[LicenseActor]
}


class LicenseActor extends Actor with ActorLogging {

  def receive: Receive = {
    case ActivateLicense(key) => sender() ! {
      val activated = DatabaseConnector.licensesRepository.activateLicense(key)

    }
    case CreateLicense(userID, activationTotal) => sender() ! createLicense(userID, activationTotal)
    case GetLicenseByID(id) => sender() ! DatabaseConnector.licensesRepository.getLicenseById(id)
    case GetLicenseByKey(key) => sender() ! DatabaseConnector.licensesRepository.getLicenseByKey(key)
    case GetLicenseByUser(userID) => sender() ! DatabaseConnector.licensesRepository.getLicenseByUserID(userID)
    case GetLicenses => sender() ! DatabaseConnector.licensesRepository.licenses
    case DeleteLicense(id) => sender() ! DatabaseConnector.licensesRepository.deleteLicense(id)
  }

  def createLicense(userID: String, activationTotal: Int): Unit ={

    val key = LicenseGenerator.createLicense(userID)

    DatabaseConnector.licensesRepository.insert(License(-1, userID, key, activationTotal, activationTotal))
  }
}
