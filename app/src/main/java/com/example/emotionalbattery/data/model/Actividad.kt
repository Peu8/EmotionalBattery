package com.example.emotionalbattery.data.model
// Representa una actividad emocional registrada por el usuario.
data class Actividad(
    val estado: String = "",
    val impacto: Int = 0,
    val titulo: String = "",
    val nota: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var firebaseId: String = ""
)
