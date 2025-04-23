package com.example.emotionalbattery.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.emotionalbattery.ui.screens.LoginScreen
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
    }
}