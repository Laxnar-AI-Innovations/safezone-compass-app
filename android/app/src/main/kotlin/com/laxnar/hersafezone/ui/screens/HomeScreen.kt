
package com.laxnar.hersafezone.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.laxnar.hersafezone.service.SosService
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sosService = SosService(context)
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home Screen",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Button(
                onClick = { navController.navigate("livemap/demo") }
            ) {
                Text("Open Live Map")
            }
        }
        
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    val result = sosService.trigger("harassment")
                    if (result.isSuccess) {
                        Toast.makeText(context, "SOS sent", Toast.LENGTH_SHORT).show()
                        // Navigate to the SOS we just created
                        result.getOrNull()?.let { sosId ->
                            navController.navigate("livemap/$sosId")
                        }
                    } else {
                        Toast.makeText(context, "Failed to send SOS", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .size(80.dp),
            containerColor = Color(0xFF009688) // Teal color
        ) {
            Text(
                text = "SOS",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }
    }
}
