package com.example.emotionalbattery.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object NewA : Screen("new_activity")
    object ActivityLog : Screen("activity_log")
    object Tips : Screen("tips")
}
