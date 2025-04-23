package com.example.emotionalbattery.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
}