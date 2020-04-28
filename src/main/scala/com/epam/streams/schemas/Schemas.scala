package com.epam.streams.schemas

import org.apache.spark.sql.types.{DateType, DoubleType, IntegerType, LongType, StringType, StructField, StructType}

/* Imran_Sarwar created on 4/13/2020 */
object Schemas {
  val WEATHER_SCHEMA = new StructType().add("lng", DoubleType)
    .add("lat", DoubleType)
    .add("avg_tmpr_f", DoubleType)
    .add("avg_tmpr_c", DoubleType)
    .add("wthr_date", DateType)
    .add("goe_hash", StringType)

  val HOTEL_SCHEMA = new StructType(Array(StructField("Id", LongType),
    StructField("Name", StringType),
    StructField("Country", StringType),
    StructField("City", StringType),
    StructField("Address", StringType),
    StructField("Latitude", DoubleType),
    StructField("Longitude", DoubleType)))

  val OUTPUT_FACT = new StructType(Array(StructField("Date", DateType),
    StructField("Temprature", DoubleType),
    StructField("Hotel_Name", StringType),
    StructField("Count", IntegerType)))

}
