package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float? = null,
    val bodyFatPercent: Float? = null,
    val muscleMassKg: Float? = null,
    val chestCm: Float? = null,
    val waistCm: Float? = null,
    val hipsCm: Float? = null,
    val bicepsCm: Float? = null,
    val thighsCm: Float? = null,
    val calvesCm: Float? = null,
    val neckCm: Float? = null,
    val shouldersCm: Float? = null,
    val notes: String = "",
    val date: Long = System.currentTimeMillis()
)

@Entity(tableName = "progress_photos")
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val category: PhotoCategory = PhotoCategory.FRONT,
    val notes: String = "",
    val weightKg: Float? = null,
    val date: Long = System.currentTimeMillis()
)

enum class PhotoCategory { FRONT, SIDE, BACK, FLEXING, OTHER }

@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val exerciseName: String,
    val recordType: RecordType,
    val value: Float,
    val date: Long = System.currentTimeMillis(),
    val previousValue: Float? = null
)

enum class RecordType { MAX_WEIGHT, MAX_REPS, MAX_DURATION, MAX_DISTANCE, MAX_VOLUME }

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val date: String,
    val steps: Int = 0,
    val caloriesBurned: Int = 0,
    val caloriesConsumed: Int = 0,
    val activeMinutes: Int = 0,
    val waterMl: Int = 0,
    val sleepHours: Float = 0f,
    val sleepQuality: Int = 0,
    val workoutCount: Int = 0,
    val totalVolume: Float = 0f,
    val mood: Int = 3,
    val streakDays: Int = 0,
    val stressLevel: Int = 0
)
