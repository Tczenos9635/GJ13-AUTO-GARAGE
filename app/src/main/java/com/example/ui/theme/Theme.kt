package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = NavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = GarageOrange,
    onSecondary = Color.White,
    secondaryContainer = GarageOrangeContainer,
    onSecondaryContainer = GarageOrangeDark,
    tertiary = GarageGreen,
    onTertiary = Color.White,
    tertiaryContainer = GarageGreenContainer,
    error = GarageRed,
    errorContainer = GarageRedContainer,
    background = SlateBackground,
    surface = CardSurface,
    onBackground = TextDark,
    onSurface = TextDark
)

private val DarkColorScheme = darkColorScheme(
    primary = NavyLight,
    onPrimary = Color.White,
    primaryContainer = NavyDark,
    secondary = GarageOrange,
    tertiary = GarageGreen,
    error = GarageRed,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
