name := "hotel-weather-stream"
scalacOptions := Seq("-Xexperimental", "-unchecked", "-deprecation")

version := "0.1.1"
//For Deployment
scalaVersion := "2.11.12"
//scalaVersion := "2.12.10"

//val sparkVersion = "3.0.0-preview"
//val vegasVersion = "0.3.11"
//val postgresVersion = "42.2.2"

//For Deployment
val sparkVersion = "2.4.0"
val vegasVersion = "0.3.11"
val postgresVersion = "42.2.2"

val kafkaVersion = "2.4.0"
val log4jVersion = "2.4.1"

resolvers ++= Seq(
  "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven",
  "Typesafe Simple Repository" at "https://repo.typesafe.com/typesafe/simple/maven-releases",
  "MavenRepository" at "https://mvnrepository.com"
)


libraryDependencies ++= Seq(
  // Kafka
  "org.apache.kafka" % "kafka-streams" % "0.11.0.0" % "provided",
  // Spark
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  // logging
  "org.apache.logging.log4j" % "log4j-api" % "2.4.1" % "provided",
  "org.apache.logging.log4j" % "log4j-core" % "2.4.1" % "provided",
  // postgres for DB connectivity
  "org.postgresql" % "postgresql" % postgresVersion % "provided",
  // https://mvnrepository.com/artifact/ch.hsr/geohash
  "ch.hsr" % "geohash" % "1.4.0",

  // logging
  "org.apache.logging.log4j" % "log4j-api" % log4jVersion % "provided",
  "org.apache.logging.log4j" % "log4j-core" % log4jVersion % "provided"


// Kafka
//"org.apache.kafka" % "kafka-streams" % "0.11.0.0",
//// Spark
//"org.apache.spark" %% "spark-core" % sparkVersion,
//"org.apache.spark" %% "spark-sql" % sparkVersion,
//// logging
//"org.apache.logging.log4j" % "log4j-api" % "2.4.1",
//"org.apache.logging.log4j" % "log4j-core" % "2.4.1",
//// postgres for DB connectivity
//"org.postgresql" % "postgresql" % postgresVersion,
//// https://mvnrepository.com/artifact/ch.hsr/geohash
//"ch.hsr" % "geohash" % "1.4.0"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"