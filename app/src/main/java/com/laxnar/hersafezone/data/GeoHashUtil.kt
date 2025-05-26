
package com.laxnar.hersafezone.data

import ch.hsr.geohash.GeoHash

object GeoHashUtil {
    
    fun encode(lat: Double, lng: Double, length: Int = 8): String {
        return GeoHash.geoHashStringWithCharacterPrecision(lat, lng, length)
    }
    
    fun getBoundingBox(lat: Double, lng: Double, precision: Int = 5): Pair<String, String> {
        val centerHash = encode(lat, lng, precision)
        val geoHash = GeoHash.fromGeohashString(centerHash)
        val boundingBox = geoHash.boundingBox
        
        val startHash = encode(boundingBox.minLat, boundingBox.minLon, precision)
        val endHash = encode(boundingBox.maxLat, boundingBox.maxLon, precision)
        
        return Pair(startHash, endHash)
    }
}
