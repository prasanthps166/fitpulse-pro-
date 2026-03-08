package com.fitpulse.pro.domain.nutrition

import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.MealEntry

class NutritionStatsMutator {

    fun applyMeal(currentStats: DailyStats?, today: String, meal: MealEntry): DailyStats {
        return (currentStats ?: DailyStats(date = today)).copy(
            caloriesConsumed = (currentStats?.caloriesConsumed ?: 0) + meal.calories
        )
    }

    fun applyWater(currentStats: DailyStats?, today: String, amountMl: Int): DailyStats {
        return (currentStats ?: DailyStats(date = today)).copy(
            waterMl = (currentStats?.waterMl ?: 0) + amountMl
        )
    }

    fun replaceNutritionTotals(
        currentStats: DailyStats?,
        today: String,
        caloriesConsumed: Int,
        waterMl: Int
    ): DailyStats {
        return (currentStats ?: DailyStats(date = today)).copy(
            caloriesConsumed = caloriesConsumed,
            waterMl = waterMl
        )
    }
}
