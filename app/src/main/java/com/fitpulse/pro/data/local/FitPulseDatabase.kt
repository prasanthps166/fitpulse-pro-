package com.fitpulse.pro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.Challenge
import com.fitpulse.pro.data.model.Converters
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.MeditationSession
import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.ProgressPhoto
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.data.model.WaterIntake
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutTemplate

internal const val FITPULSE_DATABASE_VERSION = 4
internal const val FITPULSE_DATABASE_NAME = "fitpulse_database"

@Database(
    entities = [
        UserProfile::class,
        Exercise::class,
        Workout::class,
        WorkoutTemplate::class,
        MealEntry::class,
        WaterIntake::class,
        BodyMeasurement::class,
        ProgressPhoto::class,
        PersonalRecord::class,
        DailyStats::class,
        Achievement::class,
        Challenge::class,
        MeditationSession::class
    ],
    version = FITPULSE_DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class FitPulseDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun mealEntryDao(): MealEntryDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun meditationSessionDao(): MeditationSessionDao

    companion object {
        @Volatile
        private var INSTANCE: FitPulseDatabase? = null

        fun getDatabase(context: Context): FitPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        fun resetInstance() {
            INSTANCE = null
        }

        private fun buildDatabase(context: Context): FitPulseDatabase {
            return Room.databaseBuilder(
                context,
                FitPulseDatabase::class.java,
                FITPULSE_DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
