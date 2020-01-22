package com.knoldus.app

import java.util.Properties

import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{GenericData}

import scala.io.Source
import org.apache.avro.generic.GenericRecord
object StreamApp extends App {

  // schema registry url.// schema registry url.

  val url = "http://broker1:8081"
  // associated topic name.
  val topic = "page-view-event"
  // avro schema avsc file path.
  val schemaPath = "/avro/page-view-event.avsc"
  // subject convention is "<topic-name>-value"
  val subject = topic + "-value"
  // avsc json string.


  import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
  import org.apache.avro.Schema
  import java.io.FileInputStream

//  val inputStream: FileInputStream = new FileInputStream(schemaPath)
//  val schema = IOUtils.toString(inputStream)
//  finally inputStream.close()
  val schema: Schema = new Parser().parse(Source.fromURL(getClass.getResource(schemaPath)).mkString)
//  val avroSchema = new Schema.Parser().parse("schema")

  val client = new CachedSchemaRegistryClient(url, 20)
val genericUser: GenericRecord = new GenericData.Record(schema)

  client.register(subject, schema)
}
