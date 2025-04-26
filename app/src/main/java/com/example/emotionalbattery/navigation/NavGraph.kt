package com.example.emotionalbattery.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.emotionalbattery.ui.screens.ActivityLogScreen
import com.example.emotionalbattery.ui.screens.HomeScreen
import com.example.emotionalbattery.ui.screens.LoginScreen
import com.example.emotionalbattery.ui.screens.NewAScreen
import com.example.emotionalbattery.ui.screens.TipsScreen

//import com.example.emotionalbattery.ui.screens.HomeScreen
//import com.example.emotionalbattery.ui.screens.RegistroScreen
//import com.example.emotionalbattery.ui.screens.ConsejosScreen
//import com.example.emotionalbattery.ui.screens.DetalleRegistroScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(route = Screen.NewA.route) {
            NewAScreen(navController)
        }
        composable(route = Screen.ActivityLog.route) {
            ActivityLogScreen(navController)
        }
        composable(route = Screen.Tips.route) {
            TipsScreen(navController)
        }
    }
}
