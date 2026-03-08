package com.fitpulse.pro.ui.screens.progress

import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.RecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressInsightsTest {

    @Test
    fun buildProgressInsights_highlightsConsistencyAndWeightTrend() {
        val weeklyStats = listOf(
            DailyStats(date = "2026-03-01", workoutCount = 1, activeMinutes = 30, totalVolume = 1200f),
            DailyStats(date = "2026-03-02", workoutCount = 0, activeMinutes = 20),
            DailyStats(date = "2026-03-03", workoutCount = 1, activeMinutes = 35, totalVolume = 1500f),
            DailyStats(date = "2026-03-04", workoutCount = 0, activeMinutes = 10),
            DailyStats(date = "2026-03-05", workoutCount = 1, activeMinutes = 40, totalVolume = 1800f),
            DailyStats(date = "2026-03-06", workoutCount = 1, activeMinutes = 35, totalVolume = 1700f),
            DailyStats(date = "2026-03-07", workoutCount = 0, activeMinutes = 25)
        )
        val measurements = listOf(
            BodyMeasurement(date = 1L, weightKg = 82f),
            BodyMeasurement(date = 2L, weightKg = 80.8f)
        )

        val insights = buildProgressInsights(
            weeklyStats = weeklyStats,
            totalWorkouts = 12,
            maxStreak = 16,
            measurements = measurements,
            personalRecords = listOf(
                PersonalRecord(
                    exerciseId = 1,
                    exerciseName = "Bench Press",
                    recordType = RecordType.MAX_WEIGHT,
                    value = 95f,
                    previousValue = 92.5f,
                    date = 3L
                )
            )
        )

        assertEquals("Consistency is strong", insights.first().title)
        assertTrue(insights.any { it.title == "Fat-loss trend looks supported" })
        assertTrue(insights.any { it.title == "A new strength marker was logged" })
        assertTrue(insights.any { it.title == "Your streak shows momentum" })
        assertTrue(insights.all { it.relatedArticleId != null })
    }

    @Test
    fun buildProgressInsights_defaultsToCautionWhenDataIsThin() {
        val insights = buildProgressInsights(
            weeklyStats = emptyList(),
            totalWorkouts = 0,
            maxStreak = 0,
            measurements = emptyList(),
            personalRecords = emptyList()
        )

        assertEquals("Show-up rate needs work", insights.first().title)
        assertTrue(insights.any { it.title == "Measurement trend needs more data" })
    }

    @Test
    fun buildMeasurementTrendContext_detectsImprovingComposition() {
        val context = buildMeasurementTrendContext(
            listOf(
                BodyMeasurement(date = 1L, weightKg = 80f, waistCm = 86f),
                BodyMeasurement(date = 2L, weightKg = 80.2f, waistCm = 84.2f)
            )
        )

        assertEquals("Waist is moving down", context.title)
        assertEquals(ProgressInsightTone.POSITIVE, context.tone)
    }

    @Test
    fun buildProgressInsights_highlightsStrengthAndStreakRecovery() {
        val weeklyStats = listOf(
            DailyStats(date = "2026-03-01", workoutCount = 0, activeMinutes = 0, streakDays = 0),
            DailyStats(date = "2026-03-02", workoutCount = 1, activeMinutes = 35, totalVolume = 1200f, streakDays = 1),
            DailyStats(date = "2026-03-03", workoutCount = 1, activeMinutes = 40, totalVolume = 1400f, streakDays = 2),
            DailyStats(date = "2026-03-04", workoutCount = 1, activeMinutes = 45, totalVolume = 1500f, streakDays = 3)
        )

        val insights = buildProgressInsights(
            weeklyStats = weeklyStats,
            totalWorkouts = 10,
            maxStreak = 8,
            measurements = listOf(BodyMeasurement(date = 1L, weightKg = 82f)),
            personalRecords = listOf(
                PersonalRecord(
                    exerciseId = 1,
                    exerciseName = "Deadlift",
                    recordType = RecordType.MAX_WEIGHT,
                    value = 160f,
                    previousValue = 155f,
                    date = 4L
                ),
                PersonalRecord(
                    exerciseId = 2,
                    exerciseName = "Pull-Up",
                    recordType = RecordType.MAX_REPS,
                    value = 12f,
                    previousValue = 11f,
                    date = 3L
                )
            )
        )

        assertTrue(insights.any { it.title == "Strength is moving up" })
        assertTrue(insights.any { it.title == "Your streak is back" })
    }

    @Test
    fun buildProgressStatusSummary_marksStrongWeeksAsImproving() {
        val weeklyStats = listOf(
            DailyStats(date = "2026-03-01", workoutCount = 1, activeMinutes = 35, totalVolume = 1200f, steps = 8500),
            DailyStats(date = "2026-03-02", workoutCount = 0, activeMinutes = 25, steps = 7200),
            DailyStats(date = "2026-03-03", workoutCount = 1, activeMinutes = 45, totalVolume = 1800f, steps = 9000),
            DailyStats(date = "2026-03-04", workoutCount = 0, activeMinutes = 20, steps = 6900),
            DailyStats(date = "2026-03-05", workoutCount = 1, activeMinutes = 40, totalVolume = 1700f, steps = 8100)
        )

        val status = buildProgressStatusSummary(
            weeklyStats = weeklyStats,
            totalWorkouts = 20,
            maxStreak = 10,
            measurements = emptyList()
        )

        assertEquals("You are improving", status.title)
        assertEquals(ProgressTrajectory.IMPROVING, status.trajectory)
    }

    @Test
    fun buildProgressSummaryMetrics_exposesTrainingVolumeAndAdherence() {
        val weeklyStats = listOf(
            DailyStats(date = "2026-03-01", workoutCount = 1, activeMinutes = 30, totalVolume = 1200f, steps = 8000),
            DailyStats(date = "2026-03-02", workoutCount = 0, activeMinutes = 0, totalVolume = 0f, caloriesConsumed = 2100, waterMl = 2500),
            DailyStats(date = "2026-03-03", workoutCount = 1, activeMinutes = 35, totalVolume = 1500f, steps = 7800)
        )

        val metrics = buildProgressSummaryMetrics(weeklyStats)

        assertEquals(3, metrics.size)
        assertEquals("Training Days", metrics[0].label)
        assertEquals("Weekly Volume", metrics[1].label)
        assertEquals("Tracked Days", metrics[2].label)
    }
}
