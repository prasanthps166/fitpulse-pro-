package com.fitpulse.pro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitpulse.pro.data.model.Workout
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts WHERE isTemplate = 0 ORDER BY createdAt DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE isTemplate = 0 ORDER BY createdAt DESC")
    suspend fun getAllWorkoutsSync(): List<Workout>

    @Query("SELECT * FROM workouts WHERE isTemplate = 0 ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentWorkouts(limit: Int): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?

    @Query("SELECT * FROM workouts WHERE isTemplate = 0 AND createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    fun getWorkoutsBetweenDates(startDate: Long, endDate: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE isTemplate = 0 AND createdAt BETWEEN :startDate AND :endDate ORDER BY createdAt DESC")
    suspend fun getWorkoutsBetweenDatesSync(startDate: Long, endDate: Long): List<Workout>

    @Query("SELECT COUNT(*) FROM workouts WHERE isTemplate = 0")
    fun getTotalWorkoutCount(): Flow<Int>

    @Query("SELECT SUM(totalVolume) FROM workouts WHERE isTemplate = 0")
    fun getTotalVolume(): Flow<Float?>

    @Query("SELECT SUM(totalCalories) FROM workouts WHERE isTemplate = 0")
    fun getTotalCaloriesBurned(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}
