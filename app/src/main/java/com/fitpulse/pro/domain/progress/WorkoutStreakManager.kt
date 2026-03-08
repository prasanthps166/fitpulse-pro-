package com.fitpulse.pro.domain.progress

import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.data.time.CurrentDayMonitor
import kotlinx.coroutines.flow.first

data class StreakUpdateResult(
    val streak: Int,
    val shouldAwardBonusXp: Boolean
)

class WorkoutStreakManager(
    private val repository: FitPulseRepository,
    private val workoutStreakCalculator: WorkoutStreakCalculator,
    private val currentDayMonitor: CurrentDayMonitor
) {

    suspend fun updateWorkoutStreak(): StreakUpdateResult {
        val recentStats = repository.getRecentStats(MAX_STREAK_WINDOW_DAYS).first()
        currentDayMonitor.refresh()
        val today = currentDayMonitor.currentDay.value.key
        val streak = workoutStreakCalculator.calculate(
            recentStats = recentStats,
            maxDays = MAX_STREAK_WINDOW_DAYS
        )
        val currentStats = repository.getStatsForDate(today).first()

        if (currentStats != null) {
            repository.insertOrUpdateStats(currentStats.copy(streakDays = streak))
        }

        return StreakUpdateResult(
            streak = streak,
            shouldAwardBonusXp = streak > 1
        )
    }

    private companion object {
        const val MAX_STREAK_WINDOW_DAYS = 60
    }
}
