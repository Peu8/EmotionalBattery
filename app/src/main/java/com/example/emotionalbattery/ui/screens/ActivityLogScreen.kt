package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.components.BottomBar
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.ui.components.CalendarComponent
import com.example.emotionalbattery.ui.components.BarChartForMonth
import com.example.emotionalbattery.viewmodels.ActivityLogViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogScreen(navController: NavController) {
    val viewModel: ActivityLogViewModel = viewModel()
    val nivelesBateria by viewModel.nivelesBateria.collectAsState()

    val currentBackStack = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack.value?.destination?.route

    //Estado para controlar el mes

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    //Cargar los niveles de batería al entrar en la pantalla

    LaunchedEffect(Unit) {
        viewModel.cargarNivelesDeBateria()
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Historial",
                currentRoute = currentRoute,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        bottomBar = {

            // Mostrar BottomBar

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
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            //Tarjeta con el calendario

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                CalendarComponent(
                    nivelesPorDia = nivelesBateria,
                    currentMonth = currentMonth,
                    onMonthChange = { currentMonth = it },
                    onDayClick = { selectedDate ->

                        //Calcula el rango de milisegundos del día seleccionado
                        val calendar = Calendar.getInstance().apply {
                            set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth, 0, 0, 0)
                        }
                        val start = calendar.timeInMillis
                        val end = start + (24 * 60 * 60 * 1000 - 1)
                        navController.navigate("${Screen.DayActivities.route}/$start/$end")
                    }
                )
            }

            //Tarjeta con el gráfico de barras

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BarChartForMonth(
                        nivelesPorDia = nivelesBateria,
                        currentMonth = currentMonth
                    )
                }
            }
        }
    }
}
