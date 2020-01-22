name := "kafka_streams"

version := "0.1"

scalaVersion := "2.13.1"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += "io.confluent" at "http://packages.confluent.io/maven/"
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % "1.1.0",
  "org.apache.avro" % "avro" % "1.8.2",
  "io.confluent" % "kafka-avro-serializer" % "3.2.1",
  "org.apache.kafka" % "kafka-clients" % "1.1.0",
  "org.apache.kafka" % "kafka-streams" % "1.1.0",
  "io.confluent" % "kafka-streams-avro-serde" % "4.1.0" ,
  "ch.qos.logback" %  "logback-classic" % "1.1.7"
)
