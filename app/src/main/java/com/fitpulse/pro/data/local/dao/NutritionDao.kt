package com.fitpulse.pro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.WaterIntake
import kotlinx.coroutines.flow.Flow

@Dao
interface MealEntryDao {
    @Query("SELECT * FROM meal_entries ORDER BY date ASC")
    fun getAllMeals(): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    fun getMealsByDate(startOfDay: Long, endOfDay: Long): Flow<List<MealEntry>>

    @Query("SELECT SUM(calories) FROM meal_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTotalCaloriesForDay(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(proteinGrams) FROM meal_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTotalProteinForDay(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(carbsGrams) FROM meal_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTotalCarbsForDay(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Query("SELECT SUM(fatGrams) FROM meal_entries WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTotalFatForDay(startOfDay: Long, endOfDay: Long): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntry): Long

    @Update
    suspend fun updateMeal(meal: MealEntry)

    @Delete
    suspend fun deleteMeal(meal: MealEntry)
}

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake ORDER BY date ASC")
    fun getAllWaterIntake(): Flow<List<WaterIntake>>

    @Query("SELECT * FROM water_intake WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date ASC")
    fun getWaterIntakeByDate(startOfDay: Long, endOfDay: Long): Flow<List<WaterIntake>>

    @Query("SELECT SUM(amountMl) FROM water_intake WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTotalWaterForDay(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("SELECT * FROM water_intake WHERE date BETWEEN :startOfDay AND :endOfDay ORDER BY date DESC LIMIT 1")
    suspend fun getLatestWaterForDay(startOfDay: Long, endOfDay: Long): WaterIntake?

    @Query("DELETE FROM water_intake WHERE date BETWEEN :startOfDay AND :endOfDay")
    suspend fun deleteWaterBetweenDates(startOfDay: Long, endOfDay: Long): Int

    @Insert
    suspend fun insertWater(water: WaterIntake): Long

    @Delete
    suspend fun deleteWater(water: WaterIntake)
}
