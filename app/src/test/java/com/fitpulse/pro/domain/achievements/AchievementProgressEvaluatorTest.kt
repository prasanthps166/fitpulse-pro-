package com.fitpulse.pro.domain.achievements

import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.AchievementCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementProgressEvaluatorTest {

    private val evaluator = AchievementProgressEvaluator()
    private val snapshot = AchievementProgressSnapshot(
        workoutCount = 12,
        totalVolume = 12500f,
        maxStreak = 9,
        personalRecordCount = 3,
        meditationSessionCount = 4
    )

    @Test
    fun evaluate_unlocksWorkoutAchievementWhenRequirementMet() {
        val achievement = Achievement(
            id = "10_workouts",
            name = "Dedicated",
            description = "Complete 10 workouts",
            category = AchievementCategory.WORKOUT_COUNT,
            iconName = "fitness_center",
            requirement = 10
        )

        val result = evaluator.evaluate(achievement, snapshot, unlockedAtMillis = 5000L)

        assertNotNull(result)
        assertTrue(result?.wasUnlocked == true)
        assertTrue(result?.achievement?.isUnlocked == true)
        assertEquals(10, result?.achievement?.currentProgress)
        assertEquals(5000L, result?.achievement?.unlockedAt)
    }

    @Test
    fun evaluate_updatesMeditationProgressWithoutUnlocking() {
        val achievement = Achievement(
            id = "meditation_starter",
            name = "Mindful",
            description = "Complete 5 meditation sessions",
            category = AchievementCategory.SPECIAL,
            iconName = "self_improvement",
            requirement = 5
        )

        val result = evaluator.evaluate(achievement, snapshot, unlockedAtMillis = 5000L)

        assertNotNull(result)
        assertFalse(result?.wasUnlocked ?: true)
        assertEquals(4, result?.achievement?.currentProgress)
        assertFalse(result?.achievement?.isUnlocked ?: true)
    }

    @Test
    fun evaluate_ignoresUnsupportedNutritionAchievements() {
        val achievement = Achievement(
            id = "log_meals_7",
            name = "Meal Tracker",
            description = "Log meals for 7 days",
            category = AchievementCategory.NUTRITION,
            iconName = "restaurant",
            requirement = 7
        )

        val result = evaluator.evaluate(achievement, snapshot, unlockedAtMillis = 5000L)

        assertNull(result)
    }
}
