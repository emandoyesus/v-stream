package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioDarkColorScheme = darkColorScheme(
    primary = AcidCitron,
    onPrimary = AcidCitronText,
    primaryContainer = AcidCitronMuted,
    onPrimaryContainer = AcidCitron,
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF0A0C10),
    secondaryContainer = StudioCardElevated,
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = SignalRose,
    onTertiary = Color.White,
    background = StudioBlack,
    onBackground = TextPrimary,
    surface = StudioCard,
    onSurface = TextPrimary,
    surfaceVariant = StudioCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    outlineVariant = StudioBorderActive
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = StudioDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioBlack.toArgb()
                window.navigationBarColor = StudioBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
