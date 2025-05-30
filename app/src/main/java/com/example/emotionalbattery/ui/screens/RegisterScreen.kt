package com.example.emotionalbattery.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.emotionalbattery.navigation.Screen
import com.example.emotionalbattery.ui.theme.AppTextFieldShape
import com.example.emotionalbattery.viewmodels.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {
    //Estados locales para los campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    val uiState by registerViewModel.uiState.collectAsState()

    //Navegar al login despues del registro

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Register.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Registrate para poder comenzar a disfrutar de Emotional Battery",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        //Campo de email

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape
        )
        Spacer(Modifier.height(8.dp))

        //Campo de teléfono

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Número de teléfono") },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape
        )
        Spacer(Modifier.height(8.dp))

        //Campo de contraseña

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(8.dp))

        //Campo de confirmación de contraseña

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = AppTextFieldShape,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))

        //Botón de registro

        Button(
            onClick = {
                registerViewModel.registrarUsuario(email, telefono, password, confirmPassword)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        Spacer(Modifier.height(16.dp))

        //Mensaje de error o validación

        uiState.message?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        //Indicador de carga

        if (uiState.isLoading) {
            Spacer(Modifier.height(8.dp))
            CircularProgressIndicator()
        }

        //Botón para cambiar a la pantalla de login

        TextButton(
            onClick = { navController.navigate(Screen.Login.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("¿Ya tienes cuenta? Iniciar sesión")
        }
    }
}
