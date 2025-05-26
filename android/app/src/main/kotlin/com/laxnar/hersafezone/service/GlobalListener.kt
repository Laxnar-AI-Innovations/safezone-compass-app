
package com.laxnar.hersafezone.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.laxnar.hersafezone.MainActivity
import com.laxnar.hersafezone.R
import com.laxnar.hersafezone.data.FirestoreSchema
import com.laxnar.hersafezone.data.GeoHashUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.TimeUnit

class GlobalListener(private val context: Context) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    private var listenerRegistration: ListenerRegistration? = null
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "sos_alerts"
        private const val NOTIFICATION_ID = 1001
        private const val MAX_DISTANCE_METERS = 2000f // 2 km
    }
    
    init {
        createNotificationChannel()
    }
    
    fun startListening() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val location = getCurrentLocation()
                val boundingBox = GeoHashUtil.getBoundingBox(location.latitude, location.longitude, 5)
                
                listenerRegistration = firestore.collection(FirestoreSchema.SOS)
                    .whereGreaterThanOrEqualTo(
                        "${FirestoreSchema.SOSFields.LOCATION}.${FirestoreSchema.SOSFields.LocationFields.GEOHASH}",
                        boundingBox.first
                    )
                    .whereLessThanOrEqualTo(
                        "${FirestoreSchema.SOSFields.LOCATION}.${FirestoreSchema.SOSFields.LocationFields.GEOHASH}",
                        boundingBox.second
                    )
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) return@addSnapshotListener
                        
                        snapshot?.documentChanges?.forEach { change ->
                            if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                val document = change.document
                                val data = document.data
                                
                                val timestamp = data[FirestoreSchema.SOSFields.TIMESTAMP] as? com.google.firebase.Timestamp
                                val status = data[FirestoreSchema.SOSFields.STATUS] as? String
                                val locationData = data[FirestoreSchema.SOSFields.LOCATION] as? Map<String, Any>
                                
                                if (timestamp != null && status == "active" && locationData != null) {
                                    val sosLat = locationData[FirestoreSchema.SOSFields.LocationFields.LATITUDE] as? Double
                                    val sosLng = locationData[FirestoreSchema.SOSFields.LocationFields.LONGITUDE] as? Double
                                    
                                    if (sosLat != null && sosLng != null) {
                                        val sosLocation = Location("").apply {
                                            latitude = sosLat
                                            longitude = sosLng
                                        }
                                        
                                        val currentLocation = Location("").apply {
                                            latitude = location.latitude
                                            longitude = location.longitude
                                        }
                                        
                                        val distance = currentLocation.distanceTo(sosLocation)
                                        val age = Date().time - timestamp.toDate().time
                                        val hours24 = TimeUnit.HOURS.toMillis(24)
                                        
                                        if (age > hours24) {
                                            // TTL workaround - delete old documents
                                            document.reference.delete()
                                        } else if (distance <= MAX_DISTANCE_METERS) {
                                            showSOSNotification(document.id)
                                        }
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
    
    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }
    
    private suspend fun getCurrentLocation(): Location {
        return try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await() ?: fusedLocationClient.lastLocation.await() 
                ?: throw Exception("No location available")
        } catch (e: Exception) {
            fusedLocationClient.lastLocation.await() 
                ?: throw Exception("No location available")
        }
    }
    
    private fun showSOSNotification(sosId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("navigate_to", "livemap")
            putExtra("sos_id", sosId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SOS Alert Nearby")
            .setContentText("Someone nearby needs help!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "SOS Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for nearby SOS alerts"
        }
        notificationManager.createNotificationChannel(channel)
    }
}
