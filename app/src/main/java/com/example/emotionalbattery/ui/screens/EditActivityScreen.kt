package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.data.model.Actividad
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.utils.TimeSimulator
import com.example.emotionalbattery.viewmodels.ActividadViewModel
import com.example.emotionalbattery.viewmodels.EditActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(navController: NavController, activityId: String) {
    //ViewModels
    val viewModel: EditActivityViewModel = viewModel()
    val actividadViewModel: ActividadViewModel = viewModel()

    //Estado local para la actividad cargada
    var actividad by remember { mutableStateOf<Actividad?>(null) }
    var error by remember { mutableStateOf("") }

    //UI para mensajes en Snackbar

    val mensaje by actividadViewModel.mensajeUI.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    //Ruta actual para la TopBar

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    //Lista de estados emocionales y sus impactos

    val estados = listOf("Muy mal", "Mal", "Neutral", "Bien", "Muy bien")
    val valores = mapOf(
        "Muy mal" to -15,
        "Mal" to -5,
        "Neutral" to 0,
        "Bien" to 5,
        "Muy bien" to 15
    )

    //Carga  de la actividad desde Firebase
    LaunchedEffect(activityId) {
        viewModel.loadActivity(
            activityId,
            onLoaded = { actividad = it },
            onError = { error = it }
        )
    }

    //Mostrar mensajes del ViewModel en Snackbar
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            actividadViewModel.mostrarMensaje(null)
        }
    }

    //Si la actividad se cargó correctamente, se muestra el formulario de edición
    actividad?.let { act ->
        var titulo by remember { mutableStateOf(act.titulo) }
        var nota by remember { mutableStateOf(act.nota) }
        var estado by remember { mutableStateOf(act.estado) }

        Scaffold(
            topBar = { TopBar(navController, "Editar Actividad", currentRoute = currentRoute) },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }

                //Selector desplegable para estado emocional
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Me sentí") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        estados.forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est) },
                                onClick = {
                                    estado = est
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                //Campo para modificar el título de la actividad
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Actividad") },
                    modifier = Modifier.fillMaxWidth()
                )

                //Campo para modificar la nota
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nota = it },
                    label = { Text("Nota") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                //Botón para guardar los cambios
                Button(
                    onClick = {
                        val impacto = valores[estado] ?: 0
                        val updatedActivity = act.copy(
                            estado = estado,
                            impacto = impacto,
                            titulo = titulo,
                            nota = nota
                        )
                        viewModel.updateActivity(
                            activityId,
                            updatedActivity,
                            onSuccess = {
                                actividadViewModel.mostrarMensaje("Cambios guardados")
                                coroutineScope.launch {
                                    delay(1000)
                                    navController.popBackStack()
                                }
                            },
                            onError = { error = it }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }

                //Botón para eliminar la actividad
                OutlinedButton(
                    onClick = {
                        viewModel.deleteActivity(
                            actividad = act,
                            onSuccess = {
                                actividadViewModel.cargarActividadesDelDia(TimeSimulator.getCurrentTime())
                                actividadViewModel.mostrarMensaje("Actividad eliminada")
                                coroutineScope.launch {
                                    delay(1000)
                                    navController.popBackStack()
                                }
                            },
                            onError = { error = it }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar Actividad")
                }


                if (error.isNotEmpty()) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
            }
        }

    } ?: run {

       //Si hay errores de carga
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        } else {

            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}
