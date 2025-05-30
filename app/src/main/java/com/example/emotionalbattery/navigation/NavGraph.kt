package com.example.emotionalbattery.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.emotionalbattery.ui.screens.*

/**
 * Se define la estructura de navegación de la aplicación.
 * Asocia cada ruta con su correspondiente pantalla.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Pantallas principales

        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.NewA.route) {
            NewAScreen(navController)
        }
        composable(Screen.ActivityLog.route) {
            ActivityLogScreen(navController)
        }
        composable(Screen.Tips.route) {
            TipsScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.Contactos.route) {
            ContactosScreen(navController)
        }


        // Pantalla de actividades filtradas por día
        composable(
            route = "${Screen.DayActivities.route}/{startTimestamp}/{endTimestamp}",
            arguments = listOf(
                navArgument("startTimestamp") { defaultValue = "0" },
                navArgument("endTimestamp") { defaultValue = "0" }
            )
        ) { backStackEntry ->
            val startTimestamp = backStackEntry.arguments?.getString("startTimestamp")?.toLongOrNull() ?: 0L
            val endTimestamp = backStackEntry.arguments?.getString("endTimestamp")?.toLongOrNull() ?: 0L
            ActivityDayScreen(navController, startTimestamp, endTimestamp)
        }

        // Pantalla para editar una actividad existente usando su ID de Firebase
        composable(
            route = "${Screen.EditActivity.route}/{activityId}",
            arguments = listOf(
                navArgument("activityId") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            EditActivityScreen(navController, activityId)
        }
    }
}
