package com.epam.streams.geohash

import ch.hsr.geohash.GeoHash

/* Imran_Sarwar created on 4/13/2020 */
object GeoHashUtil {
  def findGeoHash(lat: Double, lng: Double): String = GeoHash.geoHashStringWithCharacterPrecision(lat, lng, 5)
}
