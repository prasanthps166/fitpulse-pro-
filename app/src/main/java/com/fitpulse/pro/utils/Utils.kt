package com.fitpulse.pro.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility functions used across the app.
 */
object Utils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val displayTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun getTodayString(): String = dateFormat.format(Date())

    fun formatDate(timestamp: Long): String = displayDateFormat.format(Date(timestamp))

    fun formatTime(timestamp: Long): String = displayTimeFormat.format(Date(timestamp))

    fun formatDayOfWeek(timestamp: Long): String = dayOfWeekFormat.format(Date(timestamp))

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }

    fun formatDurationSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", minutes, secs)
    }

    fun formatWeight(kg: Float, imperial: Boolean = false): String {
        return if (imperial) {
            String.format("%.1f lbs", kg * 2.20462f)
        } else {
            String.format("%.1f kg", kg)
        }
    }

    fun formatHeight(cm: Float, imperial: Boolean = false): String {
        return if (imperial) {
            val totalInches = cm / 2.54f
            val feet = (totalInches / 12).toInt()
            val inches = (totalInches % 12).toInt()
            "$feet'$inches\""
        } else {
            String.format("%.0f cm", cm)
        }
    }

    fun formatNumber(number: Int): String {
        return when {
            number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000f)
            number >= 1_000 -> String.format("%.1fK", number / 1_000f)
            else -> number.toString()
        }
    }

    fun formatHydrationAmount(amountMl: Int): String {
        if (amountMl < 1_000) {
            return "${amountMl}ml"
        }

        val liters = amountMl / 1_000f
        return if (amountMl % 1_000 == 0) {
            String.format(Locale.getDefault(), "%.0fL", liters)
        } else {
            String.format(Locale.getDefault(), "%.1fL", liters)
        }
    }

    fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        val heightM = heightCm / 100f
        return weightKg / (heightM * heightM)
    }

    fun getBMICategory(bmi: Float): String {
        return when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Normal"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }
    }

    fun calculateTDEE(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        isMale: Boolean,
        activityMultiplier: Float
    ): Int {
        val bmr = if (isMale) {
            10f * weightKg + 6.25f * heightCm - 5f * age + 5f
        } else {
            10f * weightKg + 6.25f * heightCm - 5f * age - 161f
        }
        return (bmr * activityMultiplier).toInt()
    }

    fun getStartOfDay(date: Date = Date()): Long {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getEndOfDay(date: Date = Date()): Long {
        val cal = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    fun getStartOfWeek(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getDaysAgo(days: Int): Long {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 6 -> "Good Night"
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            hour < 21 -> "Good Evening"
            else -> "Good Night"
        }
    }
}
