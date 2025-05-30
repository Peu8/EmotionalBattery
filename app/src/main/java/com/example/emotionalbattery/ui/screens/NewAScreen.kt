package com.example.emotionalbattery.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.data.model.Actividad
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.viewmodels.ActividadViewModel
import com.example.emotionalbattery.utils.TimeSimulator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAScreen(navController: NavController) {
    val viewModel: ActividadViewModel = viewModel()
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val coroutineScope = rememberCoroutineScope()
    val mensaje by viewModel.mensajeUI.collectAsState()

    //Estados disponibles y sus valores

    val estados = listOf("Muy bien", "Bien", "Neutral", "Mal", "Muy mal")
    val valores = mapOf(
        "Muy mal" to -15,
        "Mal" to -5,
        "Neutral" to 0,
        "Bien" to 5,
        "Muy bien" to 15
    )

    //Estados de la UI

    var estadoSeleccionado by remember { mutableStateOf(estados[2]) }
    var expanded by remember { mutableStateOf(false) }
    var actividad by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var selectedDateInMillis by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showTitleError by remember { mutableStateOf(false) }

    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route
    val modoDesarrollo = false

    val timestamp = selectedDateInMillis ?: System.currentTimeMillis()
    val fechaFormateada = remember(timestamp) {
        val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    }

    //Mostrar errores desde Firebase

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    //Mostrar mensajes del ViewModel

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mostrarMensaje(null)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Nueva Actividad",
                currentRoute = currentRoute
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Nueva Actividad", style = MaterialTheme.typography.titleLarge)

            //Selector de estado emocional

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = estadoSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Me sentí") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                estadoSeleccionado = estado
                                expanded = false
                            }
                        )
                    }
                }
            }

            //Campo obligatorio de título

            OutlinedTextField(
                value = actividad,
                onValueChange = {
                    actividad = it
                    if (it.isNotBlank()) showTitleError = false
                },
                label = { Text("Título de Actividad") },
                isError = showTitleError,
                modifier = Modifier.fillMaxWidth()
            )

            if (showTitleError) {
                Text(
                    text = "El campo título de actividad no puede estar vacío",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            //Campo de nota
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text("Nota") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            if (modoDesarrollo) {
                Text("Fecha seleccionada: $fechaFormateada")
                Button(onClick = {
                    val actual = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth, 0, 0, 0)
                            selectedDateInMillis = calendar.timeInMillis
                            TimeSimulator.fakeCurrentTime = calendar.timeInMillis
                        },
                        actual.get(Calendar.YEAR),
                        actual.get(Calendar.MONTH),
                        actual.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text("Seleccionar fecha manualmente")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            //Botones inferiores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(36.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        if (actividad.isBlank()) {
                            showTitleError = true
                            return@Button
                        }

                        val impacto = valores[estadoSeleccionado] ?: 0
                        val nuevaActividad = Actividad(
                            estado = estadoSeleccionado,
                            impacto = impacto,
                            titulo = actividad,
                            nota = nota,
                            timestamp = timestamp
                        )

                        viewModel.guardarActividad(
                            nuevaActividad,
                            onSuccess = {
                                viewModel.mostrarMensaje("Actividad guardada")
                                coroutineScope.launch {
                                    delay(1000)
                                    navController.popBackStack()
                                }
                            },
                            onError = { errorMessage = it }
                        )
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Aceptar")
                }
            }
        }
    }
}
