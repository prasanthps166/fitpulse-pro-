package com.fitpulse.pro.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// BRAND COLORS (Shared across all themes)
// ============================================================================
val Primary = Color(0xFF6C63FF)
val PrimaryVariant = Color(0xFF5A52E0)
val PrimaryLight = Color(0xFF8B83FF)
val PrimaryDark = Color(0xFF4A42C0)

val Secondary = Color(0xFF00E5FF)
val SecondaryVariant = Color(0xFF00B8D4)
val Accent = Color(0xFFFF6B6B)
val AccentLight = Color(0xFFFF8A80)

// Gradient Colors
val GradientStart = Color(0xFF6C63FF)
val GradientMiddle = Color(0xFF9D4EDD)
val GradientEnd = Color(0xFFFF6B6B)
val GradientCyan = Color(0xFF00E5FF)
val GradientGreen = Color(0xFF00E676)

// Status Colors (Shared)
val Success = Color(0xFF00E676)
val SuccessLight = Color(0xFF69F0AE)
val Warning = Color(0xFFFFAB40)
val WarningLight = Color(0xFFFFCA80)
val Error = Color(0xFFFF5252)
val ErrorLight = Color(0xFFFF8A80)
val Info = Color(0xFF448AFF)

// Chart Colors (Shared)
val ChartPurple = Color(0xFF6C63FF)
val ChartCyan = Color(0xFF00E5FF)
val ChartCoral = Color(0xFFFF6B6B)
val ChartGreen = Color(0xFF00E676)
val ChartOrange = Color(0xFFFFAB40)
val ChartPink = Color(0xFFFF4081)
val ChartYellow = Color(0xFFFFD740)

// Shimmer / Glow
val GlowPrimary = Color(0x306C63FF)
val GlowSecondary = Color(0x3000E5FF)
val GlowAccent = Color(0x30FF6B6B)
val GlowSuccess = Color(0x3000E676)

// FAB & Ripple
val FABColor = Primary
val RippleColor = Color(0x306C63FF)

// ============================================================================
// CUSTOM EXTENDED COLOR SCHEME
// ============================================================================

/**
 * Extended colors used by FitPulse Pro beyond Material3's built-in scheme.
 */
@Immutable
data class FitPulseColors(
    val background: Color,
    val backgroundElevated: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val card: Color,
    val cardElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOnPrimary: Color,
    val divider: Color,
    val border: Color,
    val borderLight: Color,
    val bottomBarBackground: Color,
    val bottomBarSelected: Color,
    val bottomBarUnselected: Color,
    val isDark: Boolean
)

val LocalFitPulseColors = staticCompositionLocalOf {
    DarkFitPulseColors // Default
}

// ============================================================================
// DARK THEME
// ============================================================================
val DarkFitPulseColors = FitPulseColors(
    background = Color(0xFF0A0A0F),
    backgroundElevated = Color(0xFF0D0D14),
    surface = Color(0xFF13131A),
    surfaceElevated = Color(0xFF1A1A24),
    card = Color(0xFF16162A),
    cardElevated = Color(0xFF1E1E35),
    textPrimary = Color(0xFFF5F5F7),
    textSecondary = Color(0xFFB0B0C0),
    textTertiary = Color(0xFF8B8BA1),
    textOnPrimary = Color(0xFFFFFFFF),
    divider = Color(0xFF2A2A3A),
    border = Color(0xFF2E2E42),
    borderLight = Color(0xFF3A3A50),
    bottomBarBackground = Color(0xFF0D0D14),
    bottomBarSelected = Primary,
    bottomBarUnselected = Color(0xFF8B8BA1),
    isDark = true
)

// ============================================================================
// LIGHT THEME
// ============================================================================
val LightFitPulseColors = FitPulseColors(
    background = Color(0xFFF5F5FA),
    backgroundElevated = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFF0F0F5),
    card = Color(0xFFFFFFFF),
    cardElevated = Color(0xFFF8F8FC),
    textPrimary = Color(0xFF1A1A2E),
    textSecondary = Color(0xFF5A5A72),
    textTertiary = Color(0xFF6F7186),
    textOnPrimary = Color(0xFFFFFFFF),
    divider = Color(0xFFE0E0EA),
    border = Color(0xFFD0D0DE),
    borderLight = Color(0xFFE8E8F0),
    bottomBarBackground = Color(0xFFFFFFFF),
    bottomBarSelected = Primary,
    bottomBarUnselected = Color(0xFF6F7186),
    isDark = false
)

// ============================================================================
// AMOLED BLACK THEME
// ============================================================================
val AmoledFitPulseColors = FitPulseColors(
    background = Color(0xFF000000),
    backgroundElevated = Color(0xFF050508),
    surface = Color(0xFF0A0A0F),
    surfaceElevated = Color(0xFF111118),
    card = Color(0xFF0D0D1A),
    cardElevated = Color(0xFF141425),
    textPrimary = Color(0xFFF5F5F7),
    textSecondary = Color(0xFFB0B0C0),
    textTertiary = Color(0xFF8B8BA1),
    textOnPrimary = Color(0xFFFFFFFF),
    divider = Color(0xFF1A1A28),
    border = Color(0xFF1E1E30),
    borderLight = Color(0xFF252540),
    bottomBarBackground = Color(0xFF000000),
    bottomBarSelected = Primary,
    bottomBarUnselected = Color(0xFF8B8BA1),
    isDark = true
)

// ============================================================================
// MIDNIGHT BLUE THEME
// ============================================================================
val MidnightFitPulseColors = FitPulseColors(
    background = Color(0xFF0A1128),
    backgroundElevated = Color(0xFF0E1630),
    surface = Color(0xFF121C3D),
    surfaceElevated = Color(0xFF1A244A),
    card = Color(0xFF152040),
    cardElevated = Color(0xFF1E2A50),
    textPrimary = Color(0xFFE8ECF5),
    textSecondary = Color(0xFFA0AAC0),
    textTertiary = Color(0xFF8A9AB8),
    textOnPrimary = Color(0xFFFFFFFF),
    divider = Color(0xFF2A3555),
    border = Color(0xFF2E3A5E),
    borderLight = Color(0xFF3A4870),
    bottomBarBackground = Color(0xFF0E1630),
    bottomBarSelected = Color(0xFF5CADFF),
    bottomBarUnselected = Color(0xFF8A9AB8),
    isDark = true
)

// ============================================================================
// BACKWARD COMPATIBILITY ALIASES
// These use Dark palette defaults. Screens should migrate to
// LocalFitPulseColors.current.xxx instead.
// ============================================================================
val BackgroundDark = DarkFitPulseColors.background
val BackgroundDarkElevated = DarkFitPulseColors.backgroundElevated
val SurfaceDark = DarkFitPulseColors.surface
val SurfaceDarkElevated = DarkFitPulseColors.surfaceElevated
val CardDark = DarkFitPulseColors.card
val CardDarkElevated = DarkFitPulseColors.cardElevated
val TextPrimary = DarkFitPulseColors.textPrimary
val TextSecondary = DarkFitPulseColors.textSecondary
val TextTertiary = DarkFitPulseColors.textTertiary
val TextOnPrimary = DarkFitPulseColors.textOnPrimary
val Divider = DarkFitPulseColors.divider
val Border = DarkFitPulseColors.border
val BorderLight = DarkFitPulseColors.borderLight
val BottomBarBackground = DarkFitPulseColors.bottomBarBackground
val BottomBarSelected = DarkFitPulseColors.bottomBarSelected
val BottomBarUnselected = DarkFitPulseColors.bottomBarUnselected
