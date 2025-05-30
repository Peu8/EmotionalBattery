package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.emotionalbattery.utils.cargarContactosDemo

//Representa el estado de la UI durante el login
data class LoginUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String? = null
)

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    //Intenta iniciar sesión con el email y contraseña
    fun iniciarSesion(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState(message = "Los campos no pueden estar vacíos")
            return
        }

        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        if (!email.matches(emailRegex)) {
            _uiState.value = LoginUiState(message = "El correo electrónico no es válido")
            return
        }

        _uiState.value = LoginUiState(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    //Carga de contactos demo
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        cargarContactosDemo(uid)
                    }

                    _uiState.value = LoginUiState(success = true, message = "Sesión iniciada con éxito")
                } else {
                    _uiState.value = LoginUiState(
                        message = task.exception?.localizedMessage ?: "Error al iniciar sesión"
                    )
                }
            }
    }
}
