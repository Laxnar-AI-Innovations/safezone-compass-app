
package com.laxnar.hersafezone.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.*
import com.laxnar.hersafezone.data.FirestoreSchema
import kotlinx.coroutines.launch

@Composable
fun LiveMapScreen(navController: NavController, sosId: String) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()
    
    var sosData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    
    LaunchedEffect(sosId) {
        firestore.collection(FirestoreSchema.SOS)
            .document(sosId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.data?.let { data ->
                    sosData = data
                    val locationData = data[FirestoreSchema.SOSFields.LOCATION] as? Map<String, Any>
                    locationData?.let {
                        val lat = it[FirestoreSchema.SOSFields.LocationFields.LATITUDE] as? Double ?: 0.0
                        val lng = it[FirestoreSchema.SOSFields.LocationFields.LONGITUDE] as? Double ?: 0.0
                        location = LatLng(lat, lng)
                    }
                    
                    // Check if resolved
                    val status = data[FirestoreSchema.SOSFields.STATUS] as? String
                    if (status == "resolved") {
                        navController.popBackStack()
                    }
                }
            }
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 15f)
    }
    
    LaunchedEffect(location) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
    }
    
    Scaffold(
        bottomBar = {
            sosData?.let { data ->
                val userId = data[FirestoreSchema.SOSFields.USER_ID] as? String
                val currentUserId = auth.currentUser?.uid
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentUserId == userId) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        firestore.collection(FirestoreSchema.SOS)
                                            .document(sosId)
                                            .update(FirestoreSchema.SOSFields.STATUS, "resolved")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("I'm Safe")
                            }
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        firestore.collection(FirestoreSchema.SOS)
                                            .document(sosId)
                                            .update("responderUid", currentUserId)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Arrived to Help")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = location),
                title = "SOS Location"
            )
        }
    }
}
