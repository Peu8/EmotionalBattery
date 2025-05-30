package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.components.BottomBar
import com.example.emotionalbattery.ui.components.FlashCard
import com.example.emotionalbattery.ui.components.TopBar
import com.example.emotionalbattery.viewmodels.TipsViewModel

@Composable
fun TipsScreen(navController: NavController, viewModel: TipsViewModel = viewModel()) {
    //Estado del consejo/frase desde el ViewModel
    val tip by viewModel.tip.collectAsState()

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                title = "Motivación",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Tarjeta que muestra la frase

            FlashCard(
                frontText = "Frase del Día",
                backText = tip.frase.ifEmpty { "..." },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            //Tarjeta que muestra el consejo

            FlashCard(
                frontText = "Consejo del Día",
                backText = tip.consejo.ifEmpty { "..." },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}
