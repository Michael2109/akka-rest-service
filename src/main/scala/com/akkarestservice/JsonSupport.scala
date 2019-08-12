package com.akkarestservice

import akka.http.scaladsl.marshalling.Marshaller
import com.akkarestservice.actors.UserRegistryActor.ActionPerformed
import com.akkarestservice.actors.{User, Users}
import com.akkarestservice.database.tables.{License, Licenses}
import spray.json.RootJsonFormat

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val licenseJsonFormat: RootJsonFormat[License] = jsonFormat4(License)

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat3(User)
  implicit val usersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed)

  implicit val licensesJsonFormat = jsonFormat1(Licenses)
}
