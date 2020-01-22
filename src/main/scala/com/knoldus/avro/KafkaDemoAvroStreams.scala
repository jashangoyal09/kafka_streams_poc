package com.knoldus.avro

import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.kstream.{KStream, Produced}

import scala.concurrent.TimeoutException
import org.apache.kafka.streams.{KafkaStreams, KeyValue, StreamsBuilder, StreamsConfig}

class KafkaDemoAvroStreams(val inputTopic:String, val outputTopic:String) {


  val builder: StreamsBuilder = new StreamsBuilder()
  var streams: Option[KafkaStreams] = None

  val streamsConfiguration: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "avro-stream-demo-topic-streams")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName())
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, classOf[SpecificAvroSerde[_ <: SpecificRecord]])
    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,  "http://localhost:8081")
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    p
  }


  def start() = {

    try {
      Runtime.getRuntime.addShutdownHook(new Thread(() => close()))

      val stringSerde: Serde[String] = Serdes.String
      val specificAvroUserSerde: Serde[User] = new SpecificAvroSerde[User]
      val specificAvroUserWithUUIDSerde: Serde[UserWithUUID] = new SpecificAvroSerde[UserWithUUID]

      val isKeySerde: Boolean = false
      specificAvroUserSerde.configure(
        Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
        "http://localhost:8081"), isKeySerde)
      specificAvroUserWithUUIDSerde.configure(
        Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
          "http://localhost:8081"), isKeySerde)


      val stream: KStream[String, User] = builder.stream(inputTopic)

      val mappedStream  =
        stream.map[String, UserWithUUID]((k,v) => {
            println("Streams saw messsage ============ ")
            println(s"Saw User ${v}")
            new KeyValue(k, UserWithUUID(v.id,v.name, java.util.UUID.randomUUID().toString()))
        })


      mappedStream.to(outputTopic, Produced.`with`(stringSerde, specificAvroUserWithUUIDSerde))
      streams = Some(new KafkaStreams(builder.build(), streamsConfiguration))
      streams.map(_.start())

    }
    catch {
      case timeOutEx: TimeoutException =>
        println("Timeout ")
        false
      case ex: Exception => ex.printStackTrace()
        println("Got error when reading message ")
        false
    }
  }

  def close(): Unit = streams.map(_.close())

}
