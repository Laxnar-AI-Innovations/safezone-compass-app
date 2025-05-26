
package com.laxnar.hersafezone.service

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.laxnar.hersafezone.data.FirestoreSchema
import com.laxnar.hersafezone.data.GeoHashUtil
import kotlinx.coroutines.tasks.await
import java.util.Date

class SosService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    suspend fun trigger(type: String): Result<String> {
        return try {
            val location = getCurrentLocation()
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))
            
            val geohash = GeoHashUtil.encode(location.latitude, location.longitude)
            
            val sosData = mapOf(
                FirestoreSchema.SOSFields.USER_ID to userId,
                FirestoreSchema.SOSFields.TIMESTAMP to Date(),
                FirestoreSchema.SOSFields.STATUS to "active",
                FirestoreSchema.SOSFields.MESSAGE to type,
                FirestoreSchema.SOSFields.LOCATION to mapOf(
                    FirestoreSchema.SOSFields.LocationFields.LATITUDE to location.latitude,
                    FirestoreSchema.SOSFields.LocationFields.LONGITUDE to location.longitude,
                    FirestoreSchema.SOSFields.LocationFields.GEOHASH to geohash,
                    FirestoreSchema.SOSFields.LocationFields.ACCURACY to location.accuracy
                ),
                FirestoreSchema.SOSFields.RESPONDERS to emptyList<String>(),
                FirestoreSchema.SOSFields.RESPONSE_COUNT to 0
            )
            
            val docRef = firestore.collection(FirestoreSchema.SOS).add(sosData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    @Suppress("MissingPermission")
    private suspend fun getCurrentLocation(): Location {
        return try {
            val locationResult = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
            
            locationResult ?: fusedLocationClient.lastLocation.await() 
                ?: throw Exception("Unable to get location")
        } catch (e: Exception) {
            fusedLocationClient.lastLocation.await() 
                ?: throw Exception("No location available")
        }
    }
}
