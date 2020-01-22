package com.knoldus.avro

import scala.io.Source

case class User(var id: Int, var name: String)
object User {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse(
    Source.fromURL(getClass.getResource("/userSchema.avsc")).mkString)
}