
package com.laxnar.hersafezone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.maps.android.compose.*
import com.laxnar.hersafezone.data.FirestoreSchema
import kotlinx.coroutines.launch
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveMapScreen(
    navController: NavController,
    sosId: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    
    var sosData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var listenerRegistration by remember { mutableStateOf<ListenerRegistration?>(null) }
    
    // Camera state for the map
    val defaultLocation = LatLng(37.7749, -122.4194) // San Francisco default
    var mapLocation by remember { mutableStateOf(defaultLocation) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapLocation, 15f)
    }
    
    // Set up real-time listener
    LaunchedEffect(sosId) {
        listenerRegistration = firestore.collection(FirestoreSchema.SOS)
            .document(sosId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                snapshot?.data?.let { data ->
                    sosData = data
                    
                    // Update map location
                    val locationData = data[FirestoreSchema.SOSFields.LOCATION] as? Map<String, Any>
                    locationData?.let { location ->
                        val lat = location[FirestoreSchema.SOSFields.LocationFields.LATITUDE] as? Double
                        val lng = location[FirestoreSchema.SOSFields.LocationFields.LONGITUDE] as? Double
                        
                        if (lat != null && lng != null) {
                            mapLocation = LatLng(lat, lng)
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(mapLocation, 15f)
                        }
                    }
                    
                    // Check if SOS is resolved and navigate back
                    val status = data[FirestoreSchema.SOSFields.STATUS] as? String
                    if (status == "resolved") {
                        navController.popBackStack()
                    }
                }
            }
    }
    
    // Cleanup listener
    DisposableEffect(Unit) {
        onDispose {
            listenerRegistration?.remove()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // SOS location marker
            Marker(
                state = MarkerState(position = mapLocation),
                title = "SOS Alert",
                snippet = sosData?.get(FirestoreSchema.SOSFields.MESSAGE) as? String ?: "Emergency"
            )
        }
        
        // Action button overlay
        sosData?.let { data ->
            val sosUserId = data[FirestoreSchema.SOSFields.USER_ID] as? String
            val isOwnSOS = currentUserId == sosUserId
            
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isOwnSOS) "Your SOS Alert" else "SOS Alert Nearby",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = data[FirestoreSchema.SOSFields.MESSAGE] as? String ?: "Emergency",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    if (isOwnSOS) {
                                        // Update status to resolved
                                        firestore.collection(FirestoreSchema.SOS)
                                            .document(sosId)
                                            .update(FirestoreSchema.SOSFields.STATUS, "resolved")
                                        
                                        Toast.makeText(context, "SOS marked as resolved", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // Add current user as responder
                                        val updates = mapOf(
                                            "${FirestoreSchema.SOSFields.RESPONDERS}" to com.google.firebase.firestore.FieldValue.arrayUnion(currentUserId),
                                            "${FirestoreSchema.SOSFields.RESPONSE_COUNT}" to com.google.firebase.firestore.FieldValue.increment(1)
                                        )
                                        
                                        firestore.collection(FirestoreSchema.SOS)
                                            .document(sosId)
                                            .update(updates)
                                        
                                        Toast.makeText(context, "Marked as responding", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOwnSOS) Color(0xFF4CAF50) else Color(0xFF2196F3)
                        )
                    ) {
                        Text(
                            text = if (isOwnSOS) "I'm Safe" else "Arrived to Help",
                            color = Color.White
                        )
                    }
                }
            }
        }
        
        // Back button
        FloatingActionButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text("←", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
