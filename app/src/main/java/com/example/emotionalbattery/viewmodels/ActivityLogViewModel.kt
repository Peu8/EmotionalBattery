package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.example.emotionalbattery.data.model.Actividad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ActivityLogViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    private val auth = FirebaseAuth.getInstance()

    private val _allActivities = MutableStateFlow<List<Actividad>>(emptyList())
    val allActivities: StateFlow<List<Actividad>> = _allActivities

    private val _dayActivities = MutableStateFlow<List<Actividad>>(emptyList())
    val dayActivities: StateFlow<List<Actividad>> = _dayActivities

    //Carga todas las actividades del usuario

    fun cargarTodasLasActividades() {
        val uid = auth.currentUser?.uid ?: return

        db.child("usuarios").child(uid).child("actividades")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = snapshot.children.mapNotNull { child ->
                        child.getValue(Actividad::class.java)?.copy(firebaseId = child.key ?: "")
                    }
                    _allActivities.value = lista
                }

                override fun onCancelled(error: DatabaseError) {
                    _allActivities.value = emptyList()
                }
            })
    }

    //Carga actividades entre dos fechas

    fun cargarActividadesDeFecha(start: Long, end: Long) {
        val uid = auth.currentUser?.uid ?: return

        db.child("usuarios").child(uid).child("actividades")
            .orderByChild("timestamp")
            .startAt(start.toDouble())
            .endAt(end.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lista = snapshot.children.mapNotNull { child ->
                        child.getValue(Actividad::class.java)?.copy(firebaseId = child.key ?: "")
                    }
                    _dayActivities.value = lista
                }

                override fun onCancelled(error: DatabaseError) {
                    _dayActivities.value = emptyList()
                }
            })
    }

    //Actualiza una actividad existente en la base de datos

    fun actualizarActividad(actividad: Actividad, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        val id = actividad.firebaseId
        if (uid == null || id.isEmpty()) {
            onError("No se puede actualizar la actividad: usuario o ID no válido")
            return
        }

        db.child("usuarios").child(uid).child("actividades").child(id)
            .setValue(actividad)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error desconocido al actualizar actividad")
            }
    }

    private val _nivelesBateria = MutableStateFlow<Map<String, Int>>(emptyMap())
    val nivelesBateria: StateFlow<Map<String, Int>> = _nivelesBateria

    //Carga los valores de batería por día

    fun cargarNivelesDeBateria() {
        val uid = auth.currentUser?.uid ?: return

        db.child("usuarios").child(uid).child("nivelesBateria")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mapa = mutableMapOf<String, Int>()
                    for (dia in snapshot.children) {
                        val valor = dia.child("valor").getValue(Int::class.java) ?: 0
                        val key = dia.key ?: continue
                        mapa[key] = valor
                    }
                    _nivelesBateria.value = mapa
                }

                override fun onCancelled(error: DatabaseError) {
                    _nivelesBateria.value = emptyMap()
                }
            })
    }
}
