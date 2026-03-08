package com.fitpulse.pro.ui.theme

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Theme modes supported by FitPulse Pro.
 */
enum class ThemeMode(val displayName: String, val description: String) {
    SYSTEM("System Default", "Follow device theme"),
    DARK("Dark", "Easy on the eyes"),
    LIGHT("Light", "Classic bright look"),
    AMOLED("AMOLED Black", "True black for OLED screens"),
    MIDNIGHT("Midnight Blue", "Deep blue accents")
}

/**
 * Manages the user's theme preference with SharedPreferences.
 */
object ThemeManager {
    private const val PREFS_NAME = "fitpulse_theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_THEME_MODE, ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        _themeMode.value = try {
            ThemeMode.valueOf(saved)
        } catch (e: Exception) {
            ThemeMode.DARK
        }
    }

    fun setTheme(context: Context, mode: ThemeMode) {
        _themeMode.value = mode
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, mode.name)
            .apply()
    }
}
