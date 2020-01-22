package com.knoldus.avro


import scala.io.Source

case class UserWithUUID(var id: Int, var name: String, var uuid: String)

object UserWithUUID {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
    Source.fromURL(getClass.getResource("/userWithUUIDSchema.avsc")).mkString)
}
