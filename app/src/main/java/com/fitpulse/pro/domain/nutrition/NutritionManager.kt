package com.fitpulse.pro.domain.nutrition

import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.WaterIntake
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.data.time.CurrentDayMonitor
import java.util.Calendar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class NutritionManager(
    private val repository: FitPulseRepository,
    private val nutritionStatsMutator: NutritionStatsMutator,
    private val currentDayMonitor: CurrentDayMonitor
) {

    val todayMeals: Flow<List<MealEntry>> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getMealsByDate(day.startMillis, day.endMillis)
    }
    val todayCalories: Flow<Int?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getTotalCaloriesForDay(day.startMillis, day.endMillis)
    }
    val todayProtein: Flow<Float?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getTotalProteinForDay(day.startMillis, day.endMillis)
    }
    val todayCarbs: Flow<Float?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getTotalCarbsForDay(day.startMillis, day.endMillis)
    }
    val todayFat: Flow<Float?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getTotalFatForDay(day.startMillis, day.endMillis)
    }
    val todayWaterEntries: Flow<List<WaterIntake>> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getWaterIntakeByDate(day.startMillis, day.endMillis)
    }
    val todayWater: Flow<Int?> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getTotalWaterForDay(day.startMillis, day.endMillis)
    }
    val weeklyMeals: Flow<List<MealEntry>> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getMealsByDate(
            startOfRollingWindow(day.startMillis, previousDays = 6),
            day.endMillis
        )
    }
    val weeklyWaterEntries: Flow<List<WaterIntake>> = currentDayMonitor.currentDay.flatMapLatest { day ->
        repository.getWaterIntakeByDate(
            startOfRollingWindow(day.startMillis, previousDays = 6),
            day.endMillis
        )
    }

    suspend fun logMeal(meal: MealEntry) {
        currentDayMonitor.refresh()
        repository.insertMeal(meal)
        syncTodayNutritionStats()
    }

    suspend fun deleteMeal(meal: MealEntry) {
        currentDayMonitor.refresh()
        repository.deleteMeal(meal)
        syncTodayNutritionStats()
    }

    suspend fun addWater(amountMl: Int) {
        currentDayMonitor.refresh()
        repository.insertWater(WaterIntake(amountMl = amountMl))
        syncTodayNutritionStats()
    }

    suspend fun undoLastWater() {
        currentDayMonitor.refresh()
        val day = currentDayMonitor.currentDay.value
        val lastEntry = repository.getLatestWaterForDay(day.startMillis, day.endMillis) ?: return
        repository.deleteWater(lastEntry)
        syncTodayNutritionStats()
    }

    suspend fun clearTodayWater() {
        currentDayMonitor.refresh()
        val day = currentDayMonitor.currentDay.value
        repository.deleteWaterBetweenDates(day.startMillis, day.endMillis)
        syncTodayNutritionStats()
    }

    private suspend fun syncTodayNutritionStats() {
        val day = currentDayMonitor.currentDay.value
        val today = day.key
        val currentStats = repository.getStatsForDate(today).first()
        val totalCalories = repository.getTotalCaloriesForDay(day.startMillis, day.endMillis).first() ?: 0
        val totalWater = repository.getTotalWaterForDay(day.startMillis, day.endMillis).first() ?: 0
        repository.insertOrUpdateStats(
            nutritionStatsMutator.replaceNutritionTotals(
                currentStats = currentStats,
                today = today,
                caloriesConsumed = totalCalories,
                waterMl = totalWater
            )
        )
    }

    private fun startOfRollingWindow(
        currentDayStartMillis: Long,
        previousDays: Int
    ): Long {
        return Calendar.getInstance().apply {
            timeInMillis = currentDayStartMillis
            add(Calendar.DAY_OF_YEAR, -previousDays)
        }.timeInMillis
    }
}


