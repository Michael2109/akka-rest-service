package com.akkarestservice.configuration

import com.typesafe.config.ConfigFactory


object Configuration {
  val config = ConfigFactory.load()
}
