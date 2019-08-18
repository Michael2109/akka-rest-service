package com.licenseserver.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.licenseserver.JsonSupport
import com.licenseserver.actors.License
import com.licenseserver.actors.LicenseActor.{ActivateLicense, CreateLicense, GetLicenseByID}
import com.licenseserver.database.DatabaseConnector
import com.licenseserver.generator.LicenseGenerator
import spray.json.JsValue

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps

trait Routes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  // other dependencies that Routes use
  def licenseActor: ActorRef

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

                  val license = (licenseActor ? GetLicenseByID(id)).mapTo[Option[License]]

                  complete(license)
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

                  val newLicense = (licenseActor ? CreateLicense(userID, totalActivations)).mapTo[License]
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

                 val licenseActivatedResult = (licenseActor ? ActivateLicense(key)).mapTo[License]

                    complete(licenseActivatedResult)

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
            complete(DatabaseConnector.licensesRepository.licenses)
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
