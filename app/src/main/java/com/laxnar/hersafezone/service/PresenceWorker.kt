
package com.laxnar.hersafezone.service

import android.content.Context
import android.location.Location
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.laxnar.hersafezone.data.FirestoreSchema
import com.laxnar.hersafezone.data.GeoHashUtil
import kotlinx.coroutines.tasks.await

class PresenceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)

    @Suppress("MissingPermission")
    override suspend fun doWork(): Result {
        return try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                return Result.failure()
            }

            val location = getCurrentLocation()
            if (location == null) {
                return Result.retry()
            }

            val fcmToken = getFcmToken()
            val geohash = GeoHashUtil.encode(location.latitude, location.longitude, 8)

            val userData = hashMapOf(
                FirestoreSchema.UserFields.LOCATION to hashMapOf(
                    FirestoreSchema.SOSFields.LocationFields.LATITUDE to location.latitude,
                    FirestoreSchema.SOSFields.LocationFields.LONGITUDE to location.longitude,
                    FirestoreSchema.SOSFields.LocationFields.GEOHASH to geohash,
                    FirestoreSchema.SOSFields.LocationFields.ACCURACY to location.accuracy
                ),
                FirestoreSchema.UserFields.FCM_TOKEN to fcmToken,
                FirestoreSchema.UserFields.LAST_SEEN to FieldValue.serverTimestamp(),
                FirestoreSchema.UserFields.IS_ACTIVE to true
            )

            firestore.collection(FirestoreSchema.USERS)
                .document(currentUser.uid)
                .set(userData, SetOptions.merge())
                .await()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    @Suppress("MissingPermission")
    private suspend fun getCurrentLocation(): Location? {
        return try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } catch (e: Exception) {
            try {
                fusedLocationClient.lastLocation.await()
            } catch (e: Exception) {
                null
            }
        }
    }

    private suspend fun getFcmToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }
}
