package com.licenseserver.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.licenseserver.Server
import com.licenseserver.actors.LicenseActor._
import com.licenseserver.database.DatabaseConnector
import com.licenseserver.generator.LicenseGenerator

import scala.concurrent.Await
import scala.concurrent.duration.Duration

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
    case ActivateLicense(key) => sender() ! activateLicense(key)
    case CreateLicense(userID, activationTotal) => sender() ! createLicense(userID, activationTotal)
    case GetLicenseByID(id) => sender() ! DatabaseConnector.licensesRepository.getLicenseById(id)
    case GetLicenseByKey(key) => sender() ! DatabaseConnector.licensesRepository.getLicenseByKey(key)
    case GetLicenseByUser(userID) => sender() ! DatabaseConnector.licensesRepository.getLicenseByUserID(userID)
    case GetLicenses => sender() ! DatabaseConnector.licensesRepository.licenses
    case DeleteLicense(id) => sender() ! DatabaseConnector.licensesRepository.deleteLicense(id)
  }

  def createLicense(userID: String, activationTotal: Int): Option[License] = {

    val key = LicenseGenerator.createLicense(userID)

    val licenseID = Await.result(DatabaseConnector.licensesRepository.insert(License(-1, userID, key, activationTotal, activationTotal)), Duration.Inf)
    Await.result(DatabaseConnector.licensesRepository.getLicenseById(licenseID), Duration.Inf)
  }

  def activateLicense(key: String): Option[License] ={
    val licenseToActivate = Await.result(DatabaseConnector.licensesRepository.getLicenseByKey(key), Duration.Inf)

    licenseToActivate match {
      case Some(license) => {
        if(license.activationsLeft > 0) {
          Await.result(DatabaseConnector.licensesRepository.updateActivationsLeftByID(license.id, license.activationsLeft - 1), Duration.Inf)
          val updatedLicense = Await.result(DatabaseConnector.licensesRepository.getLicenseById(license.id), Duration.Inf)
          updatedLicense
        } else {
          None
        }
      }
      case None => None
    }
  }
}
