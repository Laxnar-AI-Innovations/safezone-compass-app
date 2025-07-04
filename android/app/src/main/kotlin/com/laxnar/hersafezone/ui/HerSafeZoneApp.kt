package com.laxnar.hersafezone.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
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
        composable(
            route = "livemap/{sosId}",
            arguments = listOf(
                navArgument("sosId") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val sosId = backStackEntry.arguments?.getString("sosId") ?: ""
            LiveMapScreen(
                navController = navController,
                sosId = sosId
            )
        }
        composable("livemap") {
            LiveMapScreen(
                navController = navController,
                sosId = ""
            )
        }
    }
}
