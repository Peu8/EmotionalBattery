package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.emotionalbattery.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column {
        Text("Pantalla de Inicio")
        Button(onClick = { navController.navigate(Screen.NewA.route) }) {
            Text("Nueva Actividad")
        }
        Button(onClick = { navController.navigate(Screen.ActivityLog.route) }) {
            Text("Ver Registros")
        }
        Button(onClick = { navController.navigate(Screen.Tips.route) }) {
            Text("Ver Consejos")
        }
    }
}
