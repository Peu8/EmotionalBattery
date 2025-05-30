package com.example.emotionalbattery.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.navigation.Screen


 // Barra de navegación de botones inferior que permite cambiar entre las pantallas principales.

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.ActivityLog,
        Screen.Tips,
        Screen.Contactos
    )

    // Ruta actual para marcar el ítem seleccionado
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        shadowElevation = 20.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        NavigationBar {
            items.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(painterResource(id = screen.icon), contentDescription = screen.title) },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        // Navega a la pantalla seleccionada
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
