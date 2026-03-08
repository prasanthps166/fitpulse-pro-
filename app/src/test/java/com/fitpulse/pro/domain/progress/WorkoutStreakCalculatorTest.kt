package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.model.DailyStats
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class WorkoutStreakCalculatorTest {

    private val currentTime = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2025)
        set(Calendar.MONTH, Calendar.MARCH)
        set(Calendar.DAY_OF_MONTH, 7)
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val calculator = WorkoutStreakCalculator(
        currentTimeMillisProvider = { currentTime }
    )

    @Test
    fun calculate_allowsTodayToBeEmptyAndCountsPreviousConsecutiveDays() {
        val recentStats = listOf(
            DailyStats(date = "2025-03-06", workoutCount = 1),
            DailyStats(date = "2025-03-05", workoutCount = 2),
            DailyStats(date = "2025-03-04", workoutCount = 1),
            DailyStats(date = "2025-03-02", workoutCount = 1)
        )

        val streak = calculator.calculate(recentStats)

        assertEquals(3, streak)
    }

    @Test
    fun calculate_stopsAtFirstGapAfterStreakStarts() {
        val recentStats = listOf(
            DailyStats(date = "2025-03-07", workoutCount = 1),
            DailyStats(date = "2025-03-06", workoutCount = 1),
            DailyStats(date = "2025-03-04", workoutCount = 1)
        )

        val streak = calculator.calculate(recentStats)

        assertEquals(2, streak)
    }
}
