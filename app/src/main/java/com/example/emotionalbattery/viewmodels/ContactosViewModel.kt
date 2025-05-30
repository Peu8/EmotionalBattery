package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.emotionalbattery.data.model.ContactoVisible
import com.google.firebase.auth.FirebaseAuth

class ContactosViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    //Estado que contiene los contactos visibles cargados desde Firebase

    private val _contactos = MutableStateFlow<List<ContactoVisible>>(emptyList())
    val contactos: StateFlow<List<ContactoVisible>> = _contactos

    //Carga los contactos demo desde Firebase

    fun cargarContactosVisibles() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val uid = currentUser.uid

        db.child("usuarios").child(uid).child("contactos_demo")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val visibles = mutableListOf<ContactoVisible>()

                    for (contactSnap in snapshot.children) {
                        val nombre = contactSnap.child("nombre").getValue(String::class.java) ?: continue
                        val telefono = contactSnap.child("telefono").getValue(String::class.java) ?: continue
                        val estado = contactSnap.child("estado").getValue(String::class.java) ?: "Sin datos"
                        val bateria = contactSnap.child("bateria").getValue(Int::class.java) ?: 0

                        visibles.add(
                            ContactoVisible(
                                nombre = nombre,
                                telefono = telefono,
                                estado = estado,
                                bateria = bateria
                            )
                        )
                    }

                    _contactos.value = visibles
                }

                override fun onCancelled(error: DatabaseError) {
                    _contactos.value = emptyList()
                }
            })
    }
}
