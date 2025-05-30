package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emotionalbattery.R
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.theme.AppTextFieldShape
import com.example.emotionalbattery.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    //Estado de email y contraseña
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //Estado de la interfaz de usuario
    val estado by loginViewModel.uiState.collectAsState()

    //Si se inició sesión se navega a la pantalla principal
    LaunchedEffect(estado.success) {
        if (estado.success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    //Diseño principal de la pantalla de inicio de sesión

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(100.dp))


        Image(
            painter = painterResource(id = R.drawable.logo_),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(160.dp)
                .padding(bottom = 36.dp)
        )

        // Título de la pantalla

        Text(
            text = "Inicio de sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Campo de correo
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Campo de contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Botón de inicio de sesión
        Button(
            onClick = {
                loginViewModel.iniciarSesion(email, password)
            },
            enabled = !estado.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        //Botón para navegar al registro
        TextButton(
            onClick = { navController.navigate(Screen.Register.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Mostrar mensaje de error si existe
        estado.message?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        //Indicador de carga

        if (estado.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator()
        }
    }
}
