package com.fitpulse.pro.domain.home

import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachTipGeneratorTest {

    private val generator = CoachTipGenerator()

    @Test
    fun generate_prioritizesWorkoutHydrationAndStepsTips() {
        val tips = generator.generate(
            todayStats = DailyStats(date = "2025-03-01", workoutCount = 0, waterMl = 200, steps = 1200),
            profile = UserProfile(dailyWaterGoalMl = 3000)
        )

        assertEquals("tip_workout", tips.first().id)
        assertEquals(Screen.Workouts.route, tips.first().actionRoute)
        assertTrue(tips.any { it.id == "tip_water" })
        assertTrue(tips.any { it.id == "tip_steps" })
    }

    @Test
    fun generate_omitsThresholdTipsWhenDailyTargetsAreMet() {
        val tips = generator.generate(
            todayStats = DailyStats(date = "2025-03-02", workoutCount = 1, waterMl = 2500, steps = 7000),
            profile = UserProfile(dailyWaterGoalMl = 4000)
        )

        assertFalse(tips.any { it.id == "tip_workout" })
        assertFalse(tips.any { it.id == "tip_steps" })
        assertFalse(tips.any { it.id == "tip_water" })
        assertEquals(listOf("tip_form", "tip_recovery", "tip_motivation"), tips.map { it.id })
    }
}
