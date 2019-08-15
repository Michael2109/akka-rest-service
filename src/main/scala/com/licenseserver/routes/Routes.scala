package com.licenseserver.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.licenseserver.JsonSupport
import com.licenseserver.database.DatabaseConnector
import com.licenseserver.database.tables.License
import com.licenseserver.generator.LicenseGenerator
import spray.json.JsValue

import scala.concurrent.duration._
import scala.io.Source

trait Routes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  // other dependencies that Routes use
  def userRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val routes: Route =
    concat(
      pathPrefix("api") {
        pathPrefix("licenses") {
          pathPrefix("id" / IntNumber) {
            id => {
              concat {
                get {

                  val license = DatabaseConnector.getLicenseById(id)
                  license match {
                    case Some(value) => complete(value)
                    case None => complete(s"License $id not found")
                  }
                }
              }
            }
          }
        }
      },
      pathPrefix("api") {
        pathPrefix("licenses") {
          pathPrefix("add-license") {

            post {
              entity(as[JsValue]) {
                input => {

                  val userID = input.asJsObject.fields("userID").toString.drop(1).dropRight(1)
                  val totalActivations = input.asJsObject.fields("totalActivations").toString.toInt

                  val newLicense = License(-1, userID, LicenseGenerator.createLicense(userID), totalActivations, totalActivations)

                  DatabaseConnector.addLicense(newLicense)
                  complete(newLicense)
                }
              }
            }
          }
        }
      }
      ,
      pathPrefix("api") {
        pathPrefix("licenses") {
          pathPrefix("activate-license") {

            post {
              entity(as[JsValue]) {
                input => {

                  val key = input.asJsObject.fields("licenseKey").toString.drop(1).dropRight(1)

                  val success = DatabaseConnector.activateLicense(key)

                  if (success) {
                    complete(DatabaseConnector.getLicenseByKey(key))
                  } else {
                    complete(None)
                  }
                }
              }
            }
          }
        }
      }
      ,
      pathPrefix("api") {
        pathPrefix("licenses") {
          get {
            complete(DatabaseConnector.getLicenses())
          }
        }
      }
      ,
      pathPrefix("html") {
        path("licenses") {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Source.fromResource("html/licenses.html").getLines.mkString(System.lineSeparator())))
          }
        }
      },

      pathPrefix("html") {
        path("license-activator") {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Source.fromResource("html/license-activator.html").getLines.mkString(System.lineSeparator())))
          }
        }
      }
    )


}
