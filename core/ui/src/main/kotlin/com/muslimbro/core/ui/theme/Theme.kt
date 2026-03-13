package com.muslimbro.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green700,
    onPrimary = LightSurface,
    primaryContainer = Green900,
    onPrimaryContainer = Green100,
    secondary = Gold500,
    onSecondary = DarkBackground,
    secondaryContainer = Gold700,
    onSecondaryContainer = Gold100,
    background = DarkBackground,
    onBackground = LightBackground,
    surface = DarkSurface,
    onSurface = LightBackground,
    surfaceVariant = DarkSurfaceVar,
    error = androidx.compose.ui.graphics.Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = Green800,
    onPrimary = LightSurface,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Gold500,
    onSecondary = DarkBackground,
    secondaryContainer = Gold100,
    onSecondaryContainer = Gold700,
    background = LightBackground,
    onBackground = DarkBackground,
    surface = LightSurface,
    onSurface = DarkBackground,
    surfaceVariant = Green50,
    error = androidx.compose.ui.graphics.Color(0xFFB00020)
)

@Composable
fun MuslimBroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MuslimBroTypography,
        content = content
    )
}
