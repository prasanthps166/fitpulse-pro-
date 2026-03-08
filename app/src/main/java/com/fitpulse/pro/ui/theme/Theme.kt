package com.fitpulse.pro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================================================
// MATERIAL COLOR SCHEMES
// ============================================================================

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = Secondary,
    tertiary = Accent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C1A1A),
    onTertiaryContainer = AccentLight,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = ErrorLight,
    background = DarkFitPulseColors.background,
    onBackground = DarkFitPulseColors.textPrimary,
    surface = DarkFitPulseColors.surface,
    onSurface = DarkFitPulseColors.textPrimary,
    surfaceVariant = DarkFitPulseColors.surfaceElevated,
    onSurfaceVariant = DarkFitPulseColors.textSecondary,
    outline = DarkFitPulseColors.border,
    outlineVariant = DarkFitPulseColors.divider,
    inverseSurface = DarkFitPulseColors.textPrimary,
    inverseOnSurface = DarkFitPulseColors.background,
    inversePrimary = PrimaryDark,
    surfaceTint = Primary,
    scrim = Color(0x80000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8E6FF),
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryVariant,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0F5FF),
    onSecondaryContainer = Color(0xFF006878),
    tertiary = Color(0xFFE53935),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD6),
    onTertiaryContainer = Color(0xFF410002),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = LightFitPulseColors.background,
    onBackground = LightFitPulseColors.textPrimary,
    surface = LightFitPulseColors.surface,
    onSurface = LightFitPulseColors.textPrimary,
    surfaceVariant = LightFitPulseColors.surfaceElevated,
    onSurfaceVariant = LightFitPulseColors.textSecondary,
    outline = LightFitPulseColors.border,
    outlineVariant = LightFitPulseColors.divider,
    inverseSurface = Color(0xFF303034),
    inverseOnSurface = Color(0xFFF2F0F4),
    inversePrimary = PrimaryLight,
    surfaceTint = Primary,
    scrim = Color(0x33000000)
)

private val AmoledColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = Secondary,
    tertiary = Accent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C1A1A),
    onTertiaryContainer = AccentLight,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = ErrorLight,
    background = AmoledFitPulseColors.background,
    onBackground = AmoledFitPulseColors.textPrimary,
    surface = AmoledFitPulseColors.surface,
    onSurface = AmoledFitPulseColors.textPrimary,
    surfaceVariant = AmoledFitPulseColors.surfaceElevated,
    onSurfaceVariant = AmoledFitPulseColors.textSecondary,
    outline = AmoledFitPulseColors.border,
    outlineVariant = AmoledFitPulseColors.divider,
    inverseSurface = AmoledFitPulseColors.textPrimary,
    inverseOnSurface = AmoledFitPulseColors.background,
    inversePrimary = PrimaryDark,
    surfaceTint = Primary,
    scrim = Color(0x80000000)
)

private val MidnightColorScheme = darkColorScheme(
    primary = Color(0xFF5CADFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1A3A6E),
    onPrimaryContainer = Color(0xFFBBDAFF),
    secondary = Color(0xFF00E5FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF003D4D),
    onSecondaryContainer = Color(0xFF80F0FF),
    tertiary = Accent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF5C1A1A),
    onTertiaryContainer = AccentLight,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFF5C1A1A),
    onErrorContainer = ErrorLight,
    background = MidnightFitPulseColors.background,
    onBackground = MidnightFitPulseColors.textPrimary,
    surface = MidnightFitPulseColors.surface,
    onSurface = MidnightFitPulseColors.textPrimary,
    surfaceVariant = MidnightFitPulseColors.surfaceElevated,
    onSurfaceVariant = MidnightFitPulseColors.textSecondary,
    outline = MidnightFitPulseColors.border,
    outlineVariant = MidnightFitPulseColors.divider,
    inverseSurface = MidnightFitPulseColors.textPrimary,
    inverseOnSurface = MidnightFitPulseColors.background,
    inversePrimary = Color(0xFF2A5AA0),
    surfaceTint = Color(0xFF5CADFF),
    scrim = Color(0x80000000)
)

// ============================================================================
// THEME COMPOSABLE
// ============================================================================

@Composable
fun FitPulseProTheme(
    content: @Composable () -> Unit
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isSystemDark = isSystemInDarkTheme()

    val effectiveIsDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemDark
        ThemeMode.LIGHT -> false
        else -> true
    }

    val colorScheme = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemDark) DarkColorScheme else LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.MIDNIGHT -> MidnightColorScheme
    }

    val fitPulseColors = when (themeMode) {
        ThemeMode.SYSTEM -> if (isSystemDark) DarkFitPulseColors else LightFitPulseColors
        ThemeMode.DARK -> DarkFitPulseColors
        ThemeMode.LIGHT -> LightFitPulseColors
        ThemeMode.AMOLED -> AmoledFitPulseColors
        ThemeMode.MIDNIGHT -> MidnightFitPulseColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !effectiveIsDark
                isAppearanceLightNavigationBars = !effectiveIsDark
            }
        }
    }

    CompositionLocalProvider(LocalFitPulseColors provides fitPulseColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

/**
 * Convenience accessor for FitPulse custom colors.
 */
object FitPulseTheme {
    val colors: FitPulseColors
        @Composable
        get() = LocalFitPulseColors.current
}
