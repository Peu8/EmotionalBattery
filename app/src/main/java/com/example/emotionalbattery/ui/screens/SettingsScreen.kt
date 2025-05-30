package com.example.emotionalbattery.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.viewmodels.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.emotionalbattery.navigation.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.viewmodels.ActividadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    val horaGuardada by viewModel.horaInicio.collectAsState()

    val actividadViewModel: ActividadViewModel = viewModel()

    // Estados para hora y minuto seleccionados
    var hora by remember { mutableStateOf(horaGuardada.split(":").getOrNull(0) ?: "06") }
    var minuto by remember { mutableStateOf(horaGuardada.split(":").getOrNull(1) ?: "00") }

    var expandedHora by remember { mutableStateOf(false) }
    var expandedMinuto by remember { mutableStateOf(false) }

    val opcionesHora = (0..23).map { it.toString().padStart(2, '0') }
    val opcionesMinuto = (0..59).map { it.toString().padStart(2, '0') }

    var compartirEstado by remember { mutableStateOf(false) }

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    //Cargar las configuraciones al iniciar
    LaunchedEffect(Unit) {
        viewModel.cargarHoraInicio()
        viewModel.cargarCompartirEstado { compartirEstado = it }
    }

    Scaffold(
        topBar = { TopBar(navController, "Configuración", currentRoute = currentRoute) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿A qué hora comienza tu día?", style = MaterialTheme.typography.titleMedium)

            //Selección de hora y minutos

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expandedHora,
                    onExpandedChange = { expandedHora = !expandedHora }
                ) {
                    OutlinedTextField(
                        value = hora,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hora") },
                        modifier = Modifier.menuAnchor().width(100.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedHora) }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedHora,
                        onDismissRequest = { expandedHora = false }
                    ) {
                        opcionesHora.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    hora = it
                                    expandedHora = false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = expandedMinuto,
                    onExpandedChange = { expandedMinuto = !expandedMinuto }
                ) {
                    OutlinedTextField(
                        value = minuto,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Minutos") },
                        modifier = Modifier.menuAnchor().width(100.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedMinuto) }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMinuto,
                        onDismissRequest = { expandedMinuto = false }
                    ) {
                        opcionesMinuto.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    minuto = it
                                    expandedMinuto = false
                                }
                            )
                        }
                    }
                }
            }

            //Guardar hora seleccionada

            Button(onClick = {
                val nuevaHora = "$hora:$minuto"
                viewModel.guardarHoraInicio(nuevaHora)
            }) {
                Text("Guardar hora de inicio")
            }

            //Diálogo para actualizar batería inicial

            var mostrarDialogoBateria by remember { mutableStateOf(false) }

            Button(onClick = { mostrarDialogoBateria = true }) {
                Text("Actualizar batería inicial")
            }

            if (mostrarDialogoBateria) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoBateria = false },
                    title = {
                        Text(
                            text = "Selecciona tu estado actual",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
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
                                    onClick = {
                                        actividadViewModel.establecerNivelInicial(valor)
                                        mostrarDialogoBateria = false
                                    },
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

            //Opción para compartir el estado emocional

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compartir mi estado emocional")
                Switch(
                    checked = compartirEstado,
                    onCheckedChange = {
                        compartirEstado = it
                        viewModel.actualizarCompartirEstado(it)
                    }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            //Acceso al manual de usuario

            Button(
                onClick = {
                    val url = "https://drive.google.com/uc?export=download&id=124btD69EL_Cz_Hd3vkFcQEnqdx2sBqzG"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📄 Manual de Usuario")
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Botón para cerrar sesión
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }
}
