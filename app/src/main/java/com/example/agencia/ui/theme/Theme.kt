package com.example.agencia.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryTeal,
    tertiary = TertiaryIndigo,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = ColorError
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlueDark,
    secondary = SecondaryTeal,
    tertiary = TertiaryIndigo,
    background = NeutralLight,
    surface = Color.White,
    error = ColorError,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = NeutralDark,
    onSurface = NeutralDark
)

@Composable
fun PrimermvvmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our explicit theme colors for consistency
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
        typography = Typography,
        content = content
    )
}