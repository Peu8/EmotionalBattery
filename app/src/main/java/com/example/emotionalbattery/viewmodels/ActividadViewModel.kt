package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.emotionalbattery.data.model.Actividad
import com.example.emotionalbattery.utils.dateKeyDesde
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class ActividadViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val auth = FirebaseAuth.getInstance()

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    val actividades: StateFlow<List<Actividad>> = _actividades

    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel

    private val _mostrarSelectorInicioDia = MutableStateFlow(false)
    val mostrarSelectorInicioDia: StateFlow<Boolean> = _mostrarSelectorInicioDia

    private val _mensajeUI = MutableStateFlow<String?>(null)
    val mensajeUI: StateFlow<String?> = _mensajeUI

    private val _estadoInicialTexto = MutableStateFlow<String?>(null)
    val estadoInicialTexto: StateFlow<String?> = _estadoInicialTexto

    //Mensaje para mostrar en la UI
    fun mostrarMensaje(mensaje: String?) {
        _mensajeUI.value = mensaje
    }

    //Carga las actividades y el estado de batería

    fun cargarActividadesDelDia(fecha: Long) {
        val uid = auth.currentUser?.uid ?: return
        val calendar = Calendar.getInstance().apply { timeInMillis = fecha }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1

        //Actividades del día

        db.child("usuarios").child(uid).child("actividades")
            .orderByChild("timestamp")
            .startAt(startOfDay.toDouble())
            .endAt(endOfDay.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = snapshot.children.mapNotNull { it.getValue(Actividad::class.java) }
                    _actividades.value = lista.sortedByDescending { it.timestamp }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        //Nivel de batería y estado inicial del día

        val dateKey = dateKeyDesde(fecha)
        db.child("usuarios").child(uid).child("nivelesBateria").child(dateKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _batteryLevel.value = snapshot.child("valor").getValue(Int::class.java) ?: 0
                    val inicial = snapshot.child("inicial").getValue(Int::class.java)
                    _estadoInicialTexto.value = when (inicial) {
                        100 -> "Excelente (100%)"
                        80 -> "Muy Bien (80%)"
                        60 -> "Bien (60%)"
                        50 -> "Neutral (50%)"
                        40 -> "Mal (40%)"
                        20 -> "Muy Mal (20%)"
                        0 -> "Pésimo (0%)"
                        else -> null
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _batteryLevel.value = 0
                    _estadoInicialTexto.value = null
                }
            })
    }

    //Guarda una nueva actividad y actualiza el nivel de batería
    fun guardarActividad(
        actividad: Actividad,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser ?: return onError("Usuario no autenticado")

        val actividadId = db.child("usuarios").child(uid.uid).child("actividades").push().key
            ?: return onError("No se pudo generar ID de actividad")

        val actividadConId = actividad.copy(firebaseId = actividadId)
        val dateKey = dateKeyDesde(actividad.timestamp)

        val userRef = db.child("usuarios").child(uid.uid)
        val batteryRef = userRef.child("nivelesBateria").child(dateKey).child("valor")

        batteryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentLevel = snapshot.getValue(Int::class.java) ?: 0
                val newLevel = (currentLevel + actividad.impacto).coerceIn(0, 100)

                userRef.child("actividades").child(actividadId)
                    .setValue(actividadConId)
                    .addOnSuccessListener {
                        batteryRef.setValue(newLevel)
                            .addOnSuccessListener {
                                actualizarEstadoPublico(actividad.estado, newLevel)
                                mostrarMensaje("Actividad guardada")
                                cargarActividadesDelDia(System.currentTimeMillis())
                                onSuccess()
                            }
                            .addOnFailureListener { e -> onError(e.message ?: "Error actualizando nivel de batería") }
                    }
                    .addOnFailureListener { e -> onError(e.message ?: "Error guardando actividad") }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    //Verificar si se debe mostrarse el selector de inicio de día
    fun verificarInicioDelDia() {
        val uid = auth.currentUser?.uid ?: return
        val fechaHoy = dateKeyDesde(System.currentTimeMillis())

        val configRef = db.child("usuarios").child(uid).child("configuracion").child("horaInicioDia")
        val bateriaRef = db.child("usuarios").child(uid).child("nivelesBateria").child(fechaHoy)

        configRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val horaConfig = snapshot.getValue(String::class.java) ?: "06:00"
                val (h, m) = horaConfig.split(":").map { it.toInt() }

                val ahora = Calendar.getInstance()
                val horaActual = ahora.get(Calendar.HOUR_OF_DAY)
                val minutoActual = ahora.get(Calendar.MINUTE)
                val yaPaso = horaActual > h || (horaActual == h && minutoActual >= m)

                bateriaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val yaExiste = snapshot.child("inicial").exists()
                        _mostrarSelectorInicioDia.value = yaPaso && !yaExiste
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    //Establece el valor inicial de batería

    fun establecerNivelInicial(
        valor: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        val dateKey = dateKeyDesde(System.currentTimeMillis())

        val ref = db.child("usuarios").child(uid).child("nivelesBateria").child(dateKey)
        val datos = mapOf("inicial" to valor, "valor" to valor)

        ref.setValue(datos)
            .addOnSuccessListener {
                _mostrarSelectorInicioDia.value = false
                cargarActividadesDelDia(System.currentTimeMillis())
                onSuccess()
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error al guardar nivel inicial") }
    }

    // Actualiza el estado emocional público del usuario

    fun actualizarEstadoPublico(estado: String, bateria: Int) {
        val uid = auth.currentUser?.uid ?: return
        val estadoMap = mapOf("estado" to estado, "bateria" to bateria)

        db.child("usuarios").child(uid).child("estadoPublico")
            .setValue(estadoMap)
    }
}
