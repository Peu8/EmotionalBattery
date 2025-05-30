package com.example.emotionalbattery.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val AppTextFieldShape = RoundedCornerShape(22.dp)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color.White,
    secondary = Color(0xFFFFA000),
    tertiary =Color(0xFFFFA000),
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFF3EDF7),
    surfaceVariant = Color(0xFFF3EDF7),
    surfaceContainer =Color(0xFFF3EDF7),
    onBackground = Color(0xFF212121),
    onSurface = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color.White,
    secondary = Color(0xFFFFA000),
    tertiary =Color(0xFFFFA000),
    background = Color(0xFFFAFAFA),
    onSurface = Color.Black,

    surface = Color.White,
    surfaceVariant = Color.White,
    surfaceContainer =Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainerLowest = Color.White,
)

@Composable
fun EmotionalBatteryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    /*when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}
*/

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )


}

