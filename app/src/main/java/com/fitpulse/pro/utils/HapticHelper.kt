package com.fitpulse.pro.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Centralized haptic feedback system for FitPulse Pro.
 *
 * Provides distinct vibration patterns for different interaction types,
 * making the app feel responsive and alive.
 */
object HapticHelper {

    /** Light tick – used for button presses, chip toggles, tab switches */
    fun tick(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }

    /** Confirmation – used for set completion, water logged */
    fun confirm(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    /** Reject / error – used for validation failures */
    fun reject(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }

    /** Heavy click – used for PR detection, achievement unlock, level-up */
    fun heavyClick(context: Context) {
        vibrate(context, longArrayOf(0, 30, 50, 60, 40, 80), amplitudes = intArrayOf(0, 120, 0, 180, 0, 255))
    }

    /** Double pulse – used for XP gain */
    fun doublePulse(context: Context) {
        vibrate(context, longArrayOf(0, 40, 80, 40), amplitudes = intArrayOf(0, 180, 0, 180))
    }

    /** Celebration burst – used for workout finish, challenges completed */
    fun celebration(context: Context) {
        vibrate(context, longArrayOf(0, 20, 40, 40, 40, 60, 40, 80), amplitudes = intArrayOf(0, 100, 0, 150, 0, 200, 0, 255))
    }

    private fun vibrate(context: Context, timings: LongArray, amplitudes: IntArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(timings.sum())
                }
            }
        } catch (_: Exception) {
            // Silently ignore if vibration is not available
        }
    }
}
