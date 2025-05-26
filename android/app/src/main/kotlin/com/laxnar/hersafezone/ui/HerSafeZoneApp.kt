
package com.laxnar.hersafezone.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laxnar.hersafezone.ui.screens.HomeScreen
import com.laxnar.hersafezone.ui.screens.LiveMapScreen
import com.laxnar.hersafezone.ui.screens.OnboardingScreen

@Composable
fun HerSafeZoneApp() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("livemap") {
            LiveMapScreen(navController = navController)
        }
    }
}
