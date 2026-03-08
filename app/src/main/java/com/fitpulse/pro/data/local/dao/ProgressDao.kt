package com.fitpulse.pro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.PhotoCategory
import com.fitpulse.pro.data.model.ProgressPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    fun getLatestMeasurement(): Flow<BodyMeasurement?>

    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT :limit")
    fun getRecentMeasurements(limit: Int): Flow<List<BodyMeasurement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurement): Long

    @Delete
    suspend fun deleteMeasurement(measurement: BodyMeasurement)
}

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos ORDER BY date DESC")
    fun getAllPhotos(): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photos WHERE category = :category ORDER BY date DESC")
    fun getPhotosByCategory(category: PhotoCategory): Flow<List<ProgressPhoto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: ProgressPhoto): Long

    @Delete
    suspend fun deletePhoto(photo: ProgressPhoto)
}

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId ORDER BY date DESC")
    fun getRecordsForExercise(exerciseId: Long): Flow<List<PersonalRecord>>

    @Query("SELECT * FROM personal_records ORDER BY date DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<PersonalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PersonalRecord): Long

    @Delete
    suspend fun deleteRecord(record: PersonalRecord)
}

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats ORDER BY date ASC")
    fun getAllStats(): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun getStatsForDate(date: String): Flow<DailyStats?>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT :days")
    fun getRecentStats(days: Int): Flow<List<DailyStats>>

    @Query("SELECT * FROM daily_stats WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getStatsBetweenDates(startDate: String, endDate: String): Flow<List<DailyStats>>

    @Query("SELECT MAX(streakDays) FROM daily_stats")
    fun getMaxStreak(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: DailyStats)
}
