package com.example.emotionalbattery.utils

//Utilizado para realizar testeos

object TimeSimulator {
    var fakeCurrentTime: Long? = null

    fun getCurrentTime(): Long = fakeCurrentTime ?: System.currentTimeMillis()
}