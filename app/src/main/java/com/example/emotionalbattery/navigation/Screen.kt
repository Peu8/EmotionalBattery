package com.example.emotionalbattery.navigation

import androidx.annotation.DrawableRes
import com.example.emotionalbattery.R

/**
 * Representa las diferentes rutas a las pantallas de la aplicación junto con su título e ícono.
 * Se usa para estructurar la navegación y la barra inferior.
 */

sealed class Screen(
    val route: String, // Ruta utilizada en la navegación
    val title: String, // Título mostrado en la interfaz
    @DrawableRes val icon: Int // Ícono asociado a la pantalla
) {

    // Pantallas visibles en la barra inferior
    object Home : Screen("home", "Inicio", R.drawable.ic_battery)
    object ActivityLog : Screen("activity_log", "Historial", R.drawable.ic_log)
    object Tips : Screen("tips", "Motivación", R.drawable.ic_tips)
    object Contactos : Screen("contactos", "Contactos", R.drawable.ic_contactos)


    // Otras pantallas que no estan en la barra inferior
    object Login : Screen("login", "", 0)
    object Register : Screen("register", "", 0)
    object NewA : Screen("new_activity", "", 0)
    object DayActivities : Screen("day_activities", "", 0) 
    object EditActivity : Screen("edit_activity", "", 0)

    //Pantalla de configuración
    object Settings : Screen("settings", "Configuración", R.drawable.ic_settings)


}
