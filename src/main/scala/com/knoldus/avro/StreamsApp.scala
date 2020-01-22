package com.knoldus.avro

object StreamsApp extends App {
  private val inputTopic = "input-topic"
  private val outputTopic = "output-topic"

  val consumer = new KafkaDemoAvroStreams(inputTopic, outputTopic)
  consumer.start()

}
