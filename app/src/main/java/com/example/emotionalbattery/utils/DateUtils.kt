package com.example.emotionalbattery.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Genera una clave de fecha a partir de un timestamp dado y
 *      establece la hora en 00:00:00 para el valor diario.
 */

fun dateKeyDesde(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    return sdf.format(calendar.time)
}
