package com.example.emotionalbattery.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.emotionalbattery.R
import com.example.emotionalbattery.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    title: String,
    currentRoute: String?, //  Detectar la pantalla actual
    onSettingsClick: (() -> Unit)? = null //Acción opcional para el botón de configuración
) {

    // Lista de pantallas donde se muestra el icono de Home
    val mainRoutes = listOf(
        Screen.Home.route,
        Screen.ActivityLog.route,
        Screen.Tips.route,
        Screen.Contactos.route
    )

    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        //título centrado
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },

            //Íconos de navegación
            navigationIcon = {
                if (currentRoute in mainRoutes) {
                    IconButton(onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Atrás",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            },
            //Botón de configuración
            actions = {
                onSettingsClick?.let {
                    IconButton(onClick = it) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Configuración",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        )
    }
}


