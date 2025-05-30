package com.example.emotionalbattery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.compose.rememberNavController
import com.example.emotionalbattery.navigation.NavGraph
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.theme.EmotionalBatteryTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        //Modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContent {
            EmotionalBatteryTheme {
                val navController = rememberNavController()

                //Verifica si el usuario ya está autenticado

                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDestination =
                    if (currentUser != null) Screen.Home.route else Screen.Login.route

                //Configura el destino inicial de la navegación

                NavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}
