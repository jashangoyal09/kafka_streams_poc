package com.knoldus.app

import java.util.Properties

import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}


object StreamApp extends App {

  val config = {
    val properties = new Properties()
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-application")
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    properties
  }
  val builder = new KStreamBuilder()
  val sourceStream = builder.stream("SourceTopic")
  sourceStream.to("SinkTopic")
  val streams: KafkaStreams = new KafkaStreams(builder, config)
  streams.start()
}
