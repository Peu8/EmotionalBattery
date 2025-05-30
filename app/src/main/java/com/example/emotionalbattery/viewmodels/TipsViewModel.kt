package com.example.emotionalbattery.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

//Objeto con frase y consejo diario
data class Tip(
    val frase: String = "",
    val consejo: String = ""
)

class TipsViewModel : ViewModel() {

    private val _tip = MutableStateFlow(Tip())
    val tip: StateFlow<Tip> = _tip

    private val db = FirebaseDatabase.getInstance(
        "https://emotionalbatteryapp-default-rtdb.europe-west1.firebasedatabase.app"
    ).reference

    //Carga al inicializar el ViewModel
    init {
        cargarTipDelDia()
    }

    //Recupera una frase y un consejo desde Firebase
    private fun cargarTipDelDia() {
        db.child("contenidoDiario").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val frases = snapshot.child("frases").children.mapNotNull { it.getValue(String::class.java) }
                val consejos = snapshot.child("consejos").children.mapNotNull { it.getValue(String::class.java) }

                if (frases.isNotEmpty() && consejos.isNotEmpty()) {
                    val index = calcularIndiceDelDia(frases.size, consejos.size)
                    val frase = frases[index.first]
                    val consejo = consejos[index.second]
                    _tip.value = Tip(frase, consejo)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Error al obtener datos
            }
        })
    }

    //Se basa en la fecha para variar diariamente
    private fun calcularIndiceDelDia(fSize: Int, cSize: Int): Pair<Int, Int> {
        val dia = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()
        val fraseIndex = dia % fSize
        val consejoIndex = (dia / 2) % cSize
        return Pair(fraseIndex, consejoIndex)
    }
}
