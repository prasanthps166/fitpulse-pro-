package com.fitpulse.pro.data.preferences

import android.content.Context
import com.fitpulse.pro.data.model.Workout
import com.google.gson.Gson

class ActiveWorkoutStateManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun loadActiveWorkout(): Workout? {
        val serialized = prefs.getString(KEY_ACTIVE_WORKOUT, null) ?: return null
        return runCatching {
            gson.fromJson(serialized, Workout::class.java)
        }.getOrNull()
    }

    fun saveActiveWorkout(workout: Workout?) {
        if (workout == null) {
            clear()
            return
        }

        prefs.edit()
            .putString(KEY_ACTIVE_WORKOUT, gson.toJson(workout))
            .apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_ACTIVE_WORKOUT).apply()
    }

    companion object {
        private const val PREFS_NAME = "fitpulse_active_workout_prefs"
        private const val KEY_ACTIVE_WORKOUT = "active_workout"
    }
}
