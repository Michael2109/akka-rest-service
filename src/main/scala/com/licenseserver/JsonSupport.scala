package com.licenseserver

import com.licenseserver.actors.License
import com.licenseserver.database.tables.Licenses
import spray.json.RootJsonFormat

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val licenseJsonFormat: RootJsonFormat[License] = jsonFormat5(License)

  implicit val licensesJsonFormat = jsonFormat1(Licenses)
}
