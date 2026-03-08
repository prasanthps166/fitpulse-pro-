package com.fitpulse.pro.data.preferences

import android.content.Context
import com.fitpulse.pro.utils.ReminderReceiver

data class ReminderState(
    val isEnabled: Boolean = false,
    val hour: Int = 8,
    val minute: Int = 0,
    val waterRemindersEnabled: Boolean = false
)

class ReminderSettingsManager(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadState(): ReminderState {
        return ReminderState(
            isEnabled = prefs.getBoolean(KEY_WORKOUT_ENABLED, false),
            hour = prefs.getInt(KEY_WORKOUT_HOUR, 8),
            minute = prefs.getInt(KEY_WORKOUT_MINUTE, 0),
            waterRemindersEnabled = prefs.getBoolean(KEY_WATER_ENABLED, false)
        )
    }

    fun setWorkoutReminder(hour: Int, minute: Int): ReminderState {
        ReminderReceiver.scheduleWorkoutReminder(appContext, hour, minute)
        prefs.edit()
            .putBoolean(KEY_WORKOUT_ENABLED, true)
            .putInt(KEY_WORKOUT_HOUR, hour)
            .putInt(KEY_WORKOUT_MINUTE, minute)
            .apply()
        return loadState()
    }

    fun cancelWorkoutReminder(): ReminderState {
        ReminderReceiver.cancelWorkoutReminder(appContext)
        prefs.edit().putBoolean(KEY_WORKOUT_ENABLED, false).apply()
        return loadState()
    }

    fun setWaterReminders(enabled: Boolean): ReminderState {
        if (enabled) {
            ReminderReceiver.scheduleWaterReminders(appContext)
        } else {
            ReminderReceiver.cancelWaterReminders(appContext)
        }
        prefs.edit().putBoolean(KEY_WATER_ENABLED, enabled).apply()
        return loadState()
    }

    fun clearAll(): ReminderState {
        ReminderReceiver.cancelWorkoutReminder(appContext)
        ReminderReceiver.cancelWaterReminders(appContext)
        prefs.edit().clear().apply()
        return loadState()
    }

    companion object {
        private const val PREFS_NAME = "fitpulse_prefs"
        private const val KEY_WORKOUT_ENABLED = "workout_reminders_enabled"
        private const val KEY_WORKOUT_HOUR = "reminder_hour"
        private const val KEY_WORKOUT_MINUTE = "reminder_minute"
        private const val KEY_WATER_ENABLED = "water_reminders_enabled"
    }
}
