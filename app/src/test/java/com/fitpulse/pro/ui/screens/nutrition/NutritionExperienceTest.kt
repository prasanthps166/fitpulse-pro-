package com.fitpulse.pro.ui.screens.nutrition

import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.MealType
import com.fitpulse.pro.data.model.WaterIntake
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NutritionExperienceTest {

    @Test
    fun nutritionMealTemplates_exposesCoreOfflineTemplates() {
        val templates = nutritionMealTemplates()

        assertEquals(6, templates.size)
        assertTrue(templates.any { it.meal.mealType == MealType.BREAKFAST })
        assertTrue(templates.any { it.meal.mealType == MealType.POST_WORKOUT })
    }

    @Test
    fun buildDailyNutritionGuidance_prioritizesProteinWhenItIsClearlyLow() {
        val guidance = buildDailyNutritionGuidance(
            calories = 1600,
            calorieGoal = 2200,
            proteinGrams = 55f,
            proteinGoal = 160,
            waterMl = 2200,
            waterGoalMl = 3000,
            mealsLogged = 3
        )

        assertEquals(NutritionGuidanceTone.CAUTION, guidance.tone)
        assertTrue(guidance.title.contains("Protein"))
        assertTrue(guidance.nextAction.contains("protein", ignoreCase = true))
    }

    @Test
    fun buildDailyNutritionGuidance_returnsPositiveWhenCoreTargetsAreCovered() {
        val guidance = buildDailyNutritionGuidance(
            calories = 2050,
            calorieGoal = 2200,
            proteinGrams = 150f,
            proteinGoal = 160,
            waterMl = 2600,
            waterGoalMl = 3000,
            mealsLogged = 4
        )

        assertEquals(NutritionGuidanceTone.POSITIVE, guidance.tone)
        assertTrue(guidance.message.contains("Calories"))
    }

    @Test
    fun buildWeeklyNutritionSummary_countsHitDaysAcrossRollingWeek() {
        val reference = calendarAt(2026, Calendar.MARCH, 8, 12, 0).timeInMillis
        val monday = startOfDay(reference)
        val meals = listOf(
            mealAt(dayOffset = 0, dayStart = monday, calories = 2100, protein = 150f),
            mealAt(dayOffset = -1, dayStart = monday, calories = 2200, protein = 120f),
            mealAt(dayOffset = -2, dayStart = monday, calories = 1950, protein = 145f),
            mealAt(dayOffset = -3, dayStart = monday, calories = 900, protein = 40f)
        )
        val waterEntries = listOf(
            waterAt(dayOffset = 0, dayStart = monday, amountMl = 3000),
            waterAt(dayOffset = -1, dayStart = monday, amountMl = 3200),
            waterAt(dayOffset = -2, dayStart = monday, amountMl = 1800)
        )

        val summary = buildWeeklyNutritionSummary(
            meals = meals,
            waterEntries = waterEntries,
            calorieGoal = 2200,
            proteinGoal = 160,
            waterGoalMl = 3000,
            referenceTimeMillis = reference
        )

        assertEquals(7, summary.days.size)
        assertEquals(3, summary.calorieGoalDays)
        assertEquals(2, summary.proteinGoalDays)
        assertEquals(2, summary.waterGoalDays)
        assertTrue(summary.headline.contains("Protein") || summary.headline.contains("Hydration"))
    }

    private fun mealAt(
        dayOffset: Int,
        dayStart: Long,
        calories: Int,
        protein: Float
    ): MealEntry {
        return MealEntry(
            name = "Test Meal",
            mealType = MealType.LUNCH,
            calories = calories,
            proteinGrams = protein,
            carbsGrams = 40f,
            fatGrams = 12f,
            date = shiftDays(dayStart, dayOffset) + (12 * 60 * 60 * 1000L)
        )
    }

    private fun waterAt(
        dayOffset: Int,
        dayStart: Long,
        amountMl: Int
    ): WaterIntake {
        return WaterIntake(
            amountMl = amountMl,
            date = shiftDays(dayStart, dayOffset) + (9 * 60 * 60 * 1000L)
        )
    }

    private fun calendarAt(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int
    ): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun startOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun shiftDays(timestamp: Long, days: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis
    }
}
