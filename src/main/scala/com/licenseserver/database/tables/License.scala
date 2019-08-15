package com.licenseserver.database.tables

import org.joda.time.DateTime

final case class License(id: Int, userID: String, key: String, activationsLeft: Int, totalActivations: Int)
