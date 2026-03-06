package com.azyroapp.rutasmex.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkMode,
    onPrimary = BackgroundDark,
    primaryContainer = PrimaryDarkModeDark,
    onPrimaryContainer = PrimaryDarkMode,
    secondary = AccentDarkMode,
    onSecondary = BackgroundDark,
    secondaryContainer = PrimaryDarkModeDark,
    onSecondaryContainer = AccentDarkMode,
    tertiary = RouteCompleto,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Error,
    onError = BackgroundDark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Background,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Accent,
    onSecondary = Background,
    secondaryContainer = AccentLight,
    onSecondaryContainer = PrimaryDark,
    tertiary = RouteCompleto,
    onTertiary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = PrimaryLight,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = Background
)

@Composable
fun RutasMEXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Status bar color: oscuro en dark mode, primary en light mode
            window.statusBarColor = if (darkTheme) {
                android.graphics.Color.parseColor("#121212")
            } else {
                colorScheme.primary.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
