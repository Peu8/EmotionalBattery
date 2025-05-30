package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.data.model.ContactoVisible
import com.example.emotionalbattery.ui.components.BottomBar
import com.example.emotionalbattery.viewmodels.ContactosViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactosScreen(navController: NavController, viewModel: ContactosViewModel = viewModel()) {
    val contactos by viewModel.contactos.collectAsState()

    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route

    //Carga de los contactos visibles desde Firebase
    LaunchedEffect(Unit) {
        viewModel.cargarContactosVisibles()
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Contactos",
                currentRoute = currentRoute,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Muestra cada contacto con su estado actual

            items(contactos) { contacto ->
                ContactoItem(contacto)
            }
        }
    }

    //Mensaje de vacío si no hay contactos visibles

    if (contactos.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No hay contactos compartiendo su estado",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ContactoItem(contacto: ContactoVisible) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            //Nombre del contacto
            Text(
                text = contacto.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = contacto.telefono,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // Emoji y color basados en el estado emocional
        val emoji = when {
            contacto.estado == "Sin datos aún" -> "⌛"
            contacto.bateria >= 80 -> "😄"
            contacto.bateria >= 60 -> "😊"
            contacto.bateria >= 40 -> "😐"
            contacto.bateria >= 20 -> "😟"
            else -> "😞"
        }

        val estadoColor = when (contacto.estado) {
            "Muy bien" -> Color(0xFF4CAF50)
            "Bien" -> Color(0xFF8BC34A)
            "Neutral" -> Color(0xFFFFEB3B)
            "Mal" -> Color(0xFFFF9800)
            "Muy mal" -> Color(0xFFF44336)
            else -> Color.Gray
        }

        //Muestra el estado, la batería y el emoji
        Text(
            text = "${contacto.estado} (${contacto.bateria}%) $emoji",
            style = MaterialTheme.typography.bodyMedium,
            color = estadoColor
        )
    }
}
