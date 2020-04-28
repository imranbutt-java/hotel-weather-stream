package com.epam

import java.sql.Date

package object streams {
  case class Weather(lng: Double, lat: Double, avg_tmpr_f: Double, avg_tmpr_c: Double, wthr_date: Date, geo_hash: String) {}
}
