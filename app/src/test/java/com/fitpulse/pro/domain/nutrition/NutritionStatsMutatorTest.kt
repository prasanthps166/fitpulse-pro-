package com.fitpulse.pro.domain.nutrition

import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.MealType
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionStatsMutatorTest {

    private val mutator = NutritionStatsMutator()

    @Test
    fun applyMeal_createsStatsWhenMissing() {
        val updated = mutator.applyMeal(
            currentStats = null,
            today = "2025-03-01",
            meal = MealEntry(name = "Oats", mealType = MealType.BREAKFAST, calories = 420)
        )

        assertEquals("2025-03-01", updated.date)
        assertEquals(420, updated.caloriesConsumed)
    }

    @Test
    fun applyWater_accumulatesOnExistingStats() {
        val updated = mutator.applyWater(
            currentStats = DailyStats(date = "2025-03-02", waterMl = 900),
            today = "2025-03-02",
            amountMl = 250
        )

        assertEquals("2025-03-02", updated.date)
        assertEquals(1150, updated.waterMl)
    }
}
