package com.example.emotionalbattery.ui.screens

import android.print.PrintAttributes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.R
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.components.BottomBar
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.utils.TimeSimulator
import com.example.emotionalbattery.viewmodels.ActividadViewModel
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: ActividadViewModel = viewModel()
    val actividades by viewModel.actividades.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val mostrarSelectorInicioDia by viewModel.mostrarSelectorInicioDia.collectAsState()
    val estadoInicialTexto by viewModel.estadoInicialTexto.collectAsState()

    val (colorEstado, emoji) = when {
        batteryLevel >= 80 -> Color(0xFF4CAF50) to "😄"
        batteryLevel >= 60 -> Color(0xFF8BC34A) to "😊"
        batteryLevel >= 40 -> Color(0xFFFFEB3B) to "😐"
        batteryLevel >= 20 -> Color(0xFFFF9800) to "😟"
        else -> Color(0xFFF44336) to "😞"
    }

    LaunchedEffect(Unit) {
        viewModel.cargarActividadesDelDia(TimeSimulator.getCurrentTime())
        viewModel.verificarInicioDelDia()
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Inicio",
                currentRoute = currentRoute,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        bottomBar = {
            if (currentRoute in listOf(
                    Screen.Home.route,
                    Screen.ActivityLog.route,
                    Screen.Tips.route
                )
            ) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(6.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    estadoInicialTexto?.let {
                        Text(
                            text = "Has comenzado tu día: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Text(
                        text = "Estado actual: $batteryLevel% $emoji",
                        color = colorEstado,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    BatteryImage(batteryLevel)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate(Screen.NewA.route) },
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("Ingresar Actividad", style = MaterialTheme.typography.titleLarge)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Actividades de hoy", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyColumn {
                        items(actividades) { actividad ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = MaterialTheme.shapes.small,
                                tonalElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val signo = if (actividad.impacto > 0) "+" else ""
                                    Text(
                                        text = actividad.titulo,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    val (colorImpacto, emojiImpacto) = when {
                                        actividad.impacto >= 10 -> Color(0xFF4CAF50) to "😄"
                                        actividad.impacto >= 5 -> Color(0xFF8BC34A) to "😊"
                                        actividad.impacto == 0 -> Color(0xFFFFEB3B) to "😐"
                                        actividad.impacto <= -10 -> Color(0xFFF44336) to "😞"
                                        else -> Color(0xFFFF9800) to "😟"
                                    }

                                    Text(
                                        text = "$signo${actividad.impacto}% $emojiImpacto",
                                        color = colorImpacto,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarSelectorInicioDia) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(
                "¿Cómo inicias tu día hoy?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Excelente" to 100,
                        "Muy Bien" to 80,
                        "Bien" to 60,
                        "Neutral" to 50,
                        "Mal" to 40,
                        "Muy Mal" to 20,
                        "Pésimo" to 0
                    ).forEach { (estado, valor) ->
                        Button(
                            onClick = { viewModel.establecerNivelInicial(valor) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(estado)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}


@DrawableRes
fun getBatteryIcon(level: Int): Int {
    return when (level) {
        in 0..19 -> R.drawable.battery_0
        in 20..39 -> R.drawable.battery_20
        in 40..59 -> R.drawable.battery_40
        in 60..79 -> R.drawable.battery_60
        in 80..99 -> R.drawable.battery_80
        else -> R.drawable.battery_100
    }
}

@Composable
fun BatteryImage(level: Int) {
    Image(
        painter = painterResource(id = getBatteryIcon(level)),
        contentDescription = "Estado de batería",
        modifier = Modifier.size(200.dp)
    )
}
