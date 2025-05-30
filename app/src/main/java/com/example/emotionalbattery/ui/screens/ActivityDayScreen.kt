package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.data.model.Actividad
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.viewmodels.ActivityLogViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDayScreen(navController: NavController, start: Long, end: Long) {
    val viewModel: ActivityLogViewModel = viewModel()
    val activities by viewModel.dayActivities.collectAsState()

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    //Carga las actividades del rango de fechas seleccionado
    LaunchedEffect(start) {
        viewModel.cargarActividadesDeFecha(start, end)
    }

    //Formatea la fecha para mostrarla en el encabezado
    val formattedDate = remember(start) {
        val date = Date(start)
        SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(date)
    }

    //Ordena las actividades por fecha de creación
    val sortedActivities = remember(activities) {
        activities.sortedByDescending { it.timestamp }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Actividades del Día",
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Actividades del $formattedDate",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

            //Muestra cada actividad
            items(sortedActivities) { actividad ->
                ActivityItem(actividad) {
                    navController.navigate("${Screen.EditActivity.route}/${actividad.firebaseId}")
                }
            }
        }
    }
}

@Composable
fun ActivityItem(actividad: Actividad, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val signo = if (actividad.impacto > 0) "+" else ""

            //Título de la actividad

            Text(
                text = actividad.titulo,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Impacto de la actividad %

            Text(
                text = "$signo${actividad.impacto}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
