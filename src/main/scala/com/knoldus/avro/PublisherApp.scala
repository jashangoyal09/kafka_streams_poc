package com.knoldus.avro

object PublisherApp extends App {
  private val topic = "input-topic"
  val producer = new KafkaDemoAvroPublisher(topic)
  producer.send()
}

