package com.example.emotionalbattery.utils

import com.google.firebase.database.FirebaseDatabase

// Carga lista de contacto de demostración
fun cargarContactosDemo(uid: String) {
    val database = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    val contactosDemo = listOf(
        mapOf(
            "nombre" to "Juan Pérez",
            "telefono" to "+5491123456789",
            "estado" to "Bien",
            "bateria" to 70
        ),
        mapOf(
            "nombre" to "Ana Gómez",
            "telefono" to "+5491165432189",
            "estado" to "Muy bien",
            "bateria" to 90
        ),
        mapOf(
            "nombre" to "Carlos Díaz",
            "telefono" to "+5491132123456",
            "estado" to "Mal",
            "bateria" to 30
        ),
        mapOf(
            "nombre" to "Lucía Martínez",
            "telefono" to "+5491145671234",
            "estado" to "Muy mal",
            "bateria" to 10
        ),
        mapOf(
            "nombre" to "Martín Fernández",
            "telefono" to "+5491178945612",
            "estado" to "Neutral",
            "bateria" to 50
        ),
        mapOf(
            "nombre" to "Valentina López",
            "telefono" to "+5491187654321",
            "estado" to "Bien",
            "bateria" to 75
        ),
        mapOf(
            "nombre" to "Diego Torres",
            "telefono" to "+5491100123456",
            "estado" to "Muy bien",
            "bateria" to 95
        ),
        mapOf(
            "nombre" to "Sofía Ramírez",
            "telefono" to "+5491133334444",
            "estado" to "Mal",
            "bateria" to 25
        )
    )

    val contactosRef = database.child("usuarios").child(uid).child("contactos_demo")

    contactosDemo.forEachIndexed { index, contacto ->
        contactosRef.child("contacto_$index").setValue(contacto)
    }
}
