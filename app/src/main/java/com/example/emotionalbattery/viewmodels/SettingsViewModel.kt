package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    //Estado que representa la hora configurada de inicio del día

    private val _horaInicio = MutableStateFlow("06:00") // valor por defecto
    val horaInicio: StateFlow<String> = _horaInicio

    // Carga la hora de inicio del día desde la base de datos

    fun cargarHoraInicio() {
        val uid = auth.currentUser?.uid ?: return
        db.child("usuarios").child(uid).child("configuracion").child("horaInicioDia")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val hora = snapshot.getValue(String::class.java)
                    if (hora != null) {
                        _horaInicio.value = hora
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    //Guarda una nueva hora de inicio de día en la base de datos

    fun guardarHoraInicio(nuevaHora: String, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        db.child("usuarios").child(uid).child("configuracion").child("horaInicioDia")
            .setValue(nuevaHora)
            .addOnSuccessListener {
                _horaInicio.value = nuevaHora
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al guardar la hora")
            }
    }

    //Actualiza el valor de compartirEstado en la base de datos

    fun actualizarCompartirEstado(valor: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        db.child("usuarios").child(uid).child("configuracion").child("compartirEstado")
            .setValue(valor)
    }

    //Carga el valor actual de compartirEstado

    fun cargarCompartirEstado(onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        db.child("usuarios").child(uid)
            .child("configuracion").child("compartirEstado")
            .get()
            .addOnSuccessListener { snapshot ->
                val valor = snapshot.getValue(Boolean::class.java) ?: false
                onResult(valor)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

}
