package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.emotionalbattery.data.model.Actividad
import java.text.SimpleDateFormat
import java.util.*

class EditActivityViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference
    private val auth = FirebaseAuth.getInstance()

    //Carga una actividad desde Firebase por su ID

    fun loadActivity(
        activityId: String,
        onLoaded: (Actividad) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")

        db.child("usuarios").child(uid).child("actividades").child(activityId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val actividad = snapshot.getValue(Actividad::class.java)
                    if (actividad != null) {
                        onLoaded(actividad)
                    } else {
                        onError("Actividad no encontrada")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    //Actualiza una actividad en Firebase

    fun updateActivity(
        activityId: String,
        actividad: Actividad,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")

        db.child("usuarios").child(uid).child("actividades").child(activityId)
            .setValue(actividad)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Error desconocido") }
    }

    //Elimina una actividad

    fun deleteActivity(
        actividad: Actividad,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return onError("Usuario no autenticado")
        val activityId = actividad.firebaseId
        if (activityId.isEmpty()) return onError("ID de actividad inválido")

        val dateKey = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(actividad.timestamp))
        val batteryRef = db.child("usuarios").child(uid).child("nivelesBateria").child(dateKey).child("valor")
        val activityRef = db.child("usuarios").child(uid).child("actividades").child(activityId)

        //Restar el impacto de la actividad al nivel de batería
        batteryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentLevel = snapshot.getValue(Int::class.java) ?: 0
                val newLevel = (currentLevel - actividad.impacto).coerceIn(0, 100)

                activityRef.removeValue().addOnSuccessListener {
                    batteryRef.setValue(newLevel)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e.message ?: "Error al actualizar batería") }
                }.addOnFailureListener { e ->
                    onError(e.message ?: "Error al eliminar la actividad")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }
}
