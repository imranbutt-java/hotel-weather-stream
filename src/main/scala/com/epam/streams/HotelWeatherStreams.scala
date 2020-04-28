package com.epam.streams

import com.epam.streams.geohash.GeoHashUtil._
import com.epam.streams.schemas.Schemas._
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object HotelWeatherStreams {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
//      .config("spark.master", "local")
      .appName("Hotel Weather Stream")
      .getOrCreate()

    val IP: String = args(0)
    val hotelDF: DataFrame = spark.read
      .schema(HOTEL_SCHEMA)
      .option("header", "true")
      .option("sep", ",")
//        .csv("src/main/resources/geo2.csv")
      .csv(s"hdfs://$IP:8020/data/hotels/")

    import spark.implicits._

    hotelDF.show(20, true)
    val geoHashUDF: UserDefinedFunction = udf((lat: Double, lng: Double) => findGeoHash(lat, lng))

    val geoHotelDF: DataFrame = hotelDF.withColumn("hotel_geo_hash", lit(geoHashUDF(col("Latitude"), col("Longitude"))))
    geoHotelDF.show(20, true)

//    val weatherDF: DataFrame = spark.read
//      .schema(WEATHER_SCHEMA)
//      .option("dateFormat", "yyyy-mm-dd")
//      .option("header", "true")
//      .option("sep", ",")
//      .csv("src/main/resources/weather.csv")
    val weatherStream: DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$IP:9092")
      .option("subscribe", args(1))
      .option("startingOffsets", "earliest")
      .option("failOnDataLoss", false)
      .option("enable.auto.commit", false)
      .load()
      .selectExpr("cast (value as string) as json")

    val jsonOptions = Map("dateFormat" -> "yyyy-mm-dd")
    val weatherDF = weatherStream
      .select(from_json($"json", WEATHER_SCHEMA, jsonOptions)).as("data")
      .select($"data.jsontostructs(json).lng" as "lng", $"data.jsontostructs(json).lat" as "lat",
        $"data.jsontostructs(json).avg_tmpr_f" as "avg_tmpr_f", $"data.jsontostructs(json).avg_tmpr_c" as "avg_tmpr_c",
        $"data.jsontostructs(json).wthr_date" as "wthr_date", $"data.jsontostructs(json).goe_hash" as "geo_hash")

    val conditionFor5 = col("geo_hash") === col("hotel_geo_hash")
    val condtionFor4 = expr("substring(hotel_geo_hash, 1, length(hotel_geo_hash)-1) == substring(geo_hash, 1, length(geo_hash)-1)")
    val conditionFor3 = expr("substring(hotel_geo_hash, 1, length(hotel_geo_hash)-2) == substring(geo_hash, 1, length(geo_hash)-2)")

    val joinCondition = col("geo_hash") === col("hotel_geo_hash") ||
      expr("substring(hotel_geo_hash, 1, length(hotel_geo_hash)-1) == substring(geo_hash, 1, length(geo_hash)-1)") ||
      expr("substring(hotel_geo_hash, 1, length(hotel_geo_hash)-2) == substring(geo_hash, 1, length(geo_hash)-2)")

    val countCondition = when(conditionFor5, lit(5))
      .when(condtionFor4,lit(4))
      .when(conditionFor3, lit(3))

    //Streaming using windowing
    //use cache
    val result = weatherDF.selectExpr("wthr_date as date", "avg_tmpr_c as temprature", "geo_hash")
        .join(geoHotelDF, joinCondition, "left")
        .withColumn("count", countCondition)
        .selectExpr("date", "temprature", "Name as hotel_name", "count")
        .where(col("hotel_name").isNotNull)


    // optimization: may use csv or schema
    result.select(to_json(struct(col("date"), col("temprature"),
      col("hotel_name"),
      col("count"))).cast("String").as("value"))
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$IP:9092")
      .option("checkpointLocation", "checkpoints")
      .option("topic", args(2))
      .option("failOnDataLoss", false)
      .option("startingOffsets", "earliest")
      .option("enable.auto.commit", false)
      .start()
      .awaitTermination()
  }
}