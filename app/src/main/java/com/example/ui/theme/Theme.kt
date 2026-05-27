package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    secondary = DarkGreenAccent,
    onSecondary = Color.White,
    background = DeepDarkBg,
    onBackground = Color(0xFFE9EDEF),
    surface = PremiumSurfaceDark,
    onSurface = Color(0xFFE9EDEF),
    tertiary = TickBlue,
    surfaceVariant = ReceivedBubbleDark,
    onSurfaceVariant = Color(0xFF8696A0)
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    secondary = LightGreenAccent,
    onSecondary = Color(0xFF111B21),
    background = LightBackground,
    onBackground = Color(0xFF111B21),
    surface = LightSurface,
    onSurface = Color(0xFF111B21),
    tertiary = TickBlue,
    surfaceVariant = Color(0xFFEAEAEA), // light gray bubble
    onSurfaceVariant = Color(0xFF54656F)
)

@Composable
fun MyApplicationTheme(
    selectedTheme: String = "Dark", // "Light", "Dark", "System"
    content: @Composable () -> Unit
) {
    val darkTheme = when (selectedTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
