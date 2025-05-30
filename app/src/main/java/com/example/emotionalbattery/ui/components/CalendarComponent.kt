package com.example.emotionalbattery.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Componente de calendario con colores según el nivel de batería emocional.
 * Se puede navegar entre meses y días.
 */
@Composable
fun CalendarComponent(
    nivelesPorDia: Map<String, Int>,
    currentMonth: YearMonth,
    onMonthChange: (YearMonth) -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    val visibleDates: List<LocalDate> = remember(currentMonth) { getCompleteMonthGrid(currentMonth) }
    val formatter = remember { DateTimeFormatter.ofPattern("yyyyMMdd") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        //Encabezado con nombre del mes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
            }
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() } +
                        " de ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
            }
        }

        Spacer(Modifier.height(8.dp))

        //Nombres de los días de la semana
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            DayOfWeek.values().forEach {
                val name = it.getDisplayName(TextStyle.SHORT, Locale("es")).replace(".", "").uppercase()
                Text(
                    text = name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        //Celdas del calendario
        visibleDates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                week.forEach { date ->
                    val isCurrentMonth = date.month == currentMonth.month
                    val key = date.format(formatter)
                    val nivel = if (isCurrentMonth) nivelesPorDia[key] else null

                    val bgColor = when {
                        nivel == null -> Color.Transparent
                        nivel <= 33 -> Color(0xFFF44336)
                        nivel <= 66 -> Color(0xFFFFA000)
                        else -> Color(0xFF4CAF50)
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
                            .clickable(enabled = isCurrentMonth) { onDayClick(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(bgColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = when {
                                    !isCurrentMonth -> Color.Gray
                                    nivel != null -> Color.White
                                    else -> Color.Black
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

/**
 * Genera los días faltantes del mes anterior y siguiente para completar la cuadricula de un mes
 */

fun getCompleteMonthGrid(month: YearMonth): List<LocalDate> {
    val firstOfMonth = month.atDay(1)
    val lastOfMonth = month.atEndOfMonth()

    val firstDayOfWeek = DayOfWeek.MONDAY.value
    val startOffset = (firstOfMonth.dayOfWeek.value - firstDayOfWeek + 7) % 7
    val daysInMonth = lastOfMonth.dayOfMonth

    val totalCells = ((startOffset + daysInMonth + 6) / 7) * 7
    val dates = mutableListOf<LocalDate>()

    val prevMonth = month.minusMonths(1)
    val daysInPrev = prevMonth.lengthOfMonth()
    for (i in (daysInPrev - startOffset + 1)..daysInPrev) {
        dates.add(prevMonth.atDay(i))
    }

    for (i in 1..daysInMonth) {
        dates.add(month.atDay(i))
    }

    val remaining = totalCells - dates.size
    for (i in 1..remaining) {
        dates.add(month.plusMonths(1).atDay(i))
    }

    val lastWeek = dates.takeLast(7)
    if (lastWeek.none { it.month == month.month }) {
        return dates.dropLast(7)
    }

    return dates
}

/**
 * Crea una gráfica de barras contabilizando los días del mes como
 *     malos, neutrales o buenos.
 */

@Composable
fun BarChartForMonth(
    nivelesPorDia: Map<String, Int>,
    currentMonth: YearMonth
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyyMMdd") }
    val diasMes = (1..currentMonth.lengthOfMonth()).map { currentMonth.atDay(it) }

    // Clasifica los niveles por color
    val colores = diasMes.mapNotNull { fecha ->
        val key = fecha.format(formatter)
        nivelesPorDia[key]
    }

    val rojos = colores.count { it <= 33 }
    val amarillos = colores.count { it in 34..66 }
    val verdes = colores.count { it > 66 }
    val max = listOf(rojos, amarillos, verdes).maxOrNull()?.coerceAtLeast(1) ?: 1

    val barColors = listOf(Color(0xFFF44336), Color(0xFFFFA000), Color(0xFF4CAF50))
    val valores = listOf(rojos, amarillos, verdes)

    Text(
        text = "Conteo de días del Mes",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    // Área de la gráfica
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val y = size.height - 1f
            drawLine(
                color = Color.Black,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 6f
            )
        }

        // Barras verticales de la gráfica
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            valores.forEachIndexed { index, valor ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "$valor",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .fillMaxHeight(fraction = valor / max.toFloat())
                            .background(barColors[index], shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }

    // Etiquetas de las barras
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Malos", "Neutrales", "Buenos").forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Spacer(Modifier.height(24.dp))
}
