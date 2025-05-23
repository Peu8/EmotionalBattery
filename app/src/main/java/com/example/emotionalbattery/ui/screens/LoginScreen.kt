package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.emotionalbattery.navigation.Screen

@Composable
fun LoginScreen(navController: NavController) {
    Column {
        Text("Pantalla de Login")
        Button(onClick = {
            navController.navigate(Screen.Home.route)
        }) {
            Text("Iniciar sesión")
        }
    }
}
