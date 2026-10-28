package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = RoyalPurpleLight,
    onPrimary = Color.White,
    primaryContainer = MidnightIndigo,
    onPrimaryContainer = SoftLavender,
    secondary = SoftLavender,
    onSecondary = MidnightIndigo,
    tertiary = GoldAccent,
    background = MidnightIndigoDark,
    surface = MidnightIndigo,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = RoyalPurpleDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalPurple,
    onPrimary = Color.White,
    primaryContainer = PalePurpleBg,
    onPrimaryContainer = RoyalPurpleDark,
    secondary = MidnightIndigo,
    onSecondary = Color.White,
    tertiary = GoldAccent,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = SurfaceSubtle,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
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
