package com.akkarestservice.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.util.Timeout
import com.akkarestservice.JsonSupport
import com.akkarestservice.database.DatabaseConnector
import com.akkarestservice.database.tables.License
import spray.json.JsValue

import scala.concurrent.duration._
import scala.io.Source

//#user-routes-class
trait Routes extends JsonSupport {
  //#user-routes-class

  // we leave these abstract, since they will be provided by the App
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[Routes])

  // other dependencies that Routes use
  def userRegistryActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  //#all-routes
  //#users-get-post
  //#users-get-delete
  lazy val routes: Route =
  concat(
    pathPrefix("api") {
      pathPrefix("licenses") {
        pathPrefix("id" / IntNumber) {
          id => {
            concat {
              get {
                println("HERE")
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
                println(input.prettyPrint)
                val userID = input.asJsObject.fields.get("userID").get.toString
                val totalUsers =  input.asJsObject.fields.get("totalUsers").get.toString.toInt

                val newLicense = License(-1, userID, "key123", totalUsers)
                println("Adding license: " + newLicense)
                DatabaseConnector.addLicense(newLicense)
                complete(None)
              }
            }

          }
        }
      }
    }
    ,
    pathPrefix("html") {
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Source.fromResource("html/licenses.html").getLines.mkString(System.lineSeparator())))
        }
      }
    }
  )


}
