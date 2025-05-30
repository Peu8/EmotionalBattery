package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Representa el estado de la UI durante el registro
data class RegisterUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String? = null
)

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    //Registra un nuevo usuario con email, teléfono y contraseña
    fun registrarUsuario(email: String, telefono: String, password: String, confirmPassword: String) {
        if (email.isBlank() || telefono.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = RegisterUiState(message = "Todos los campos son obligatorios")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = RegisterUiState(message = "Las contraseñas no coinciden")
            return
        }

        _uiState.value = RegisterUiState(isLoading = true)

        //Crea el usuario en Firebase Authentication

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        //Guarda el número de teléfono en la base de datos
                        val db = FirebaseDatabase.getInstance(
                            "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
                        ).reference
                        db.child("usuarios").child(uid).child("telefono").setValue(telefono)
                            .addOnSuccessListener {
                                _uiState.value = RegisterUiState(success = true, message = "Registro exitoso")
                            }
                            .addOnFailureListener { e ->
                                _uiState.value = RegisterUiState(message = e.message ?: "Error al guardar teléfono")
                            }
                    }
                } else {
                    _uiState.value = RegisterUiState(
                        message = task.exception?.localizedMessage ?: "Error al registrar"
                    )
                }
            }
    }

}
