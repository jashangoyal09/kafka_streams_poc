package com.knoldus.kstream.internals

import com.sksamuel.avro4s.RecordFormat
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream._
import play.api.libs.json.{ Format, Json }

import scala.concurrent.duration.{ Duration, _ }
import scala.concurrent.{ Await, ExecutionContext, Future }

object ScalaDsl {

  def buildStreams(builder: KStreamBuilder, config: java.util.Properties): KafkaStreams = {
    new KafkaStreams(builder, config)
  }

  def startStreams(builder: KStreamBuilder, config: java.util.Properties): Unit = {
    buildStreams(builder, config).start()
  }

  def fromAvro[A](a: Any)(implicit recordFormat: RecordFormat[A]): A = a match {
    case v: GenericRecord => recordFormat.from(v)
    case _                => throw new IllegalArgumentException("Element in stream is not of type GenericRecord")
  }

  def toAvro[A](a: A)(implicit recordFormat: RecordFormat[A]): GenericRecord = {
    recordFormat.to(a)
  }

  implicit class ScalaKStream[K, V](val underlying: KStream[K, V]) extends AnyVal {

    def mapV[V1](f: V => V1): KStream[K, V1] = {
      underlying.mapValues[V1]((value: V) => f(value))
    }

    def mapAsync[V1](f: V => Future[V1])(implicit ec: ExecutionContext, duration: Duration = 60.seconds): KStream[K, V1] = {
      mapV(value => Await.result(f(value), duration))
    }

    def parseFromAvro[V1](implicit recordFormat: RecordFormat[V1]): KStream[K, V1] =
      mapV(fromAvro(_))

    def parseFromJson[V1](implicit format: Format[V1]): KStream[K, V1] = mapV {
      case v: String => Json.parse(v).as[V1]
      case _         => throw new IllegalArgumentException("Element in stream is not of type String")
    }

    def mapToAvro(implicit recordFormat: RecordFormat[V]): KStream[K, GenericRecord] = {
      mapV(toAvro(_))
    }

    def mapToJson(implicit format: Format[V]): KStream[K, String] = {
      mapV(value => Json.toJson(value).toString)
    }

    def runForeach(p: (K, V) => Unit)(implicit builder: KStreamBuilder, config: java.util.Properties): Unit = {
      underlying.foreach((key: K, value: V) => p(key, value))
      startStreams(builder, config)
    }

    def runTopic(topic: String)(implicit builder: KStreamBuilder, config: java.util.Properties): Unit = {
      underlying.to(topic)
      startStreams(builder, config)
    }
  }
}
