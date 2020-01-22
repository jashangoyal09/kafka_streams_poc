package com.knoldus.avro

import java.util.{Collections, Properties}

import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroDeserializerConfig}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.errors.TimeoutException
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaDemoAvroSubscriber(val topic:String) {

  private val props = new Properties()
  val groupId = "avro-stream"
  var shouldRun : Boolean = true

  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put("schema.registry.url", "http://localhost:8081")
  props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10000")
  props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getCanonicalName)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,classOf[KafkaAvroDeserializer].getCanonicalName)

  props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true")

  private val consumer = new KafkaConsumer[String, com.knoldus.avro.UserWithUUID](props)

  def start() = {

    try {
      Runtime.getRuntime.addShutdownHook(new Thread(() => close()))

      consumer.subscribe(Collections.singletonList(topic))

      while (shouldRun) {
        val records: ConsumerRecords[String,  com.knoldus.avro.UserWithUUID] = consumer.poll(1000)
        val it = records.iterator()
        while(it.hasNext()) {
          println("Getting message from queue.............")
          val record: ConsumerRecord[String,  com.knoldus.avro.UserWithUUID] = it.next()
          val recievedItem =record.value()
          println(s"Saw UserWithUUID ${recievedItem}")
          consumer.commitSync
        }
      }
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

  def close(): Unit = shouldRun = false
}
