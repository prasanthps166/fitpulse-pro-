package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.DailyStats
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WorkoutStreakCalculator(
    private val currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
) {

    fun calculate(recentStats: List<DailyStats>, maxDays: Int = 60): Int {
        val statsByDate = recentStats.associateBy { it.date }
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillisProvider()
        }
        var streak = 0

        repeat(maxDays) { dayOffset ->
            val dateString = formatter.format(calendar.time)
            val statsForDay = statsByDate[dateString]

            if (statsForDay != null && statsForDay.workoutCount > 0) {
                streak++
            } else if (dayOffset > 0) {
                return streak
            }

            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }
}
