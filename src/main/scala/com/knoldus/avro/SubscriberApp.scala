package com.knoldus.avro

object SubscriberApp extends App {
  private val topic = "output-topic"

  val consumer = new KafkaDemoAvroSubscriber(topic)
  consumer.start()

}
