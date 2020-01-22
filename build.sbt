name := "kafka_streams"

version := "0.1"

scalaVersion := "2.11.8"
  resolvers ++= Seq("confluent" at "http://packages.confluent.io/maven/")
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
libraryDependencies ++= Seq("org.apache.kafka" % "kafka-streams" % "2.2.0",
                              "org.apache.avro" % "avro" % "1.9.1",
  "io.confluent"%"kafka-schema-registry"%"3.3.1",
  "io.confluent"%"kafka-avro-serializer"%"3.3.1",
  "io.confluent"%"kafka-streams-avro-serde"%"5.2.0",
  "com.github.mpilquist" %% "simulacrum" % "0.10.0",
  "org.scalaz" %% "scalaz-core" % "7.2.9",
  "com.sksamuel.avro4s" %% "avro4s-core" % "1.6.4",
  "com.typesafe.play" %% "play-json" % "2.6.0-M1"

)
