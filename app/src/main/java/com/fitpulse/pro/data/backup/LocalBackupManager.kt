package com.fitpulse.pro.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.fitpulse.pro.data.local.FitPulseDatabase
import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.Challenge
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.MeditationSession
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.ProgressPhoto
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.data.model.WaterIntake
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutTemplate
import com.fitpulse.pro.data.preferences.ActiveWorkoutStateManager
import com.fitpulse.pro.data.preferences.KnowledgeLibraryStateManager
import com.fitpulse.pro.data.preferences.ReminderState
import com.fitpulse.pro.data.preferences.ReminderSettingsManager
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.utils.XPManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first

data class LocalBackupSnapshot(
    val schemaVersion: Int,
    val appVersion: String,
    val exportedAt: Long,
    val profile: UserProfile?,
    val exercises: List<Exercise>,
    val workouts: List<Workout>,
    val workoutTemplates: List<WorkoutTemplate>,
    val meals: List<MealEntry>,
    val waterEntries: List<WaterIntake>,
    val measurements: List<BodyMeasurement>,
    val progressPhotos: List<ProgressPhoto>,
    val personalRecords: List<PersonalRecord>,
    val dailyStats: List<DailyStats>,
    val achievements: List<Achievement>,
    val challenges: List<Challenge>,
    val meditationSessions: List<MeditationSession>,
    val reminderState: ReminderState,
    val savedArticleIds: List<String>,
    val lastReadArticleId: String?,
    val activeWorkout: Workout?,
    val totalXp: Int = 0,
    val lastLoginDate: String? = null
)

data class LocalBackupExportResult(
    val byteCount: Int,
    val workoutCount: Int,
    val mealCount: Int,
    val measurementCount: Int,
    val includesActiveWorkout: Boolean
)

data class LocalBackupImportResult(
    val workoutCount: Int,
    val mealCount: Int,
    val measurementCount: Int,
    val restoredActiveWorkout: Boolean,
    val restoredSavedArticles: Int
)

internal fun isSupportedBackupSchemaVersion(schemaVersion: Int): Boolean {
    return schemaVersion in MIN_SUPPORTED_BACKUP_SCHEMA_VERSION..CURRENT_BACKUP_SCHEMA_VERSION
}

class LocalBackupManager(
    context: Context,
    private val database: FitPulseDatabase,
    private val repository: FitPulseRepository,
    private val reminderSettingsManager: ReminderSettingsManager,
    private val knowledgeLibraryStateManager: KnowledgeLibraryStateManager,
    private val activeWorkoutStateManager: ActiveWorkoutStateManager
) {

    private val appContext = context.applicationContext
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val xpPrefs = appContext.getSharedPreferences(XP_PREFS_NAME, Context.MODE_PRIVATE)

    suspend fun exportTo(uri: Uri): LocalBackupExportResult {
        val snapshot = buildSnapshot()
        val serialized = gson.toJson(snapshot)
        val bytes = serialized.toByteArray(Charsets.UTF_8)

        appContext.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()
        } ?: error("Could not open backup destination.")

        return LocalBackupExportResult(
            byteCount = bytes.size,
            workoutCount = snapshot.workouts.size,
            mealCount = snapshot.meals.size,
            measurementCount = snapshot.measurements.size,
            includesActiveWorkout = snapshot.activeWorkout != null
        )
    }

    suspend fun importFrom(uri: Uri): LocalBackupImportResult {
        val serialized = appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes().toString(Charsets.UTF_8)
        } ?: error("Could not open backup file.")

        val snapshot = gson.fromJson(serialized, LocalBackupSnapshot::class.java)
            ?: error("Backup file is empty or unreadable.")

        require(isSupportedBackupSchemaVersion(snapshot.schemaVersion)) {
            "Unsupported backup version: ${snapshot.schemaVersion}"
        }

        database.withTransaction {
            database.clearAllTables()

            snapshot.profile?.let { profile ->
                repository.saveProfile(profile)
            }

            if (snapshot.exercises.isNotEmpty()) {
                repository.insertAllExercises(snapshot.exercises)
            }
            if (snapshot.workoutTemplates.isNotEmpty()) {
                repository.insertAllTemplates(snapshot.workoutTemplates)
            }
            snapshot.workouts.forEach { workout ->
                repository.insertWorkout(workout)
            }
            snapshot.meals.forEach { meal ->
                repository.insertMeal(meal)
            }
            snapshot.waterEntries.forEach { water ->
                repository.insertWater(water)
            }
            snapshot.measurements.forEach { measurement ->
                repository.insertMeasurement(measurement)
            }
            snapshot.progressPhotos.forEach { photo ->
                repository.insertPhoto(photo)
            }
            snapshot.personalRecords.forEach { record ->
                repository.insertRecord(record)
            }
            snapshot.dailyStats.forEach { stats ->
                repository.insertOrUpdateStats(stats)
            }
            if (snapshot.achievements.isNotEmpty()) {
                repository.insertAllAchievements(snapshot.achievements)
            }
            snapshot.challenges.forEach { challenge ->
                repository.insertChallenge(challenge)
            }
            snapshot.meditationSessions.forEach { session ->
                repository.insertMeditationSession(session)
            }
        }

        reminderSettingsManager.clearAll()
        if (snapshot.reminderState.isEnabled) {
            reminderSettingsManager.setWorkoutReminder(snapshot.reminderState.hour, snapshot.reminderState.minute)
        }
        reminderSettingsManager.setWaterReminders(snapshot.reminderState.waterRemindersEnabled)

        knowledgeLibraryStateManager.clearAll()
        knowledgeLibraryStateManager.setSavedArticleIds(snapshot.savedArticleIds.toSet())
        snapshot.lastReadArticleId?.let(knowledgeLibraryStateManager::setLastReadArticleId)

        activeWorkoutStateManager.saveActiveWorkout(snapshot.activeWorkout)

        xpPrefs.edit()
            .clear()
            .putInt(XPManager.KEY_TOTAL_XP, snapshot.totalXp)
            .apply()
        snapshot.lastLoginDate?.let { lastLoginDate ->
            xpPrefs.edit().putString(XPManager.KEY_LAST_LOGIN_DATE, lastLoginDate).apply()
        }

        return LocalBackupImportResult(
            workoutCount = snapshot.workouts.size,
            mealCount = snapshot.meals.size,
            measurementCount = snapshot.measurements.size,
            restoredActiveWorkout = snapshot.activeWorkout != null,
            restoredSavedArticles = snapshot.savedArticleIds.size
        )
    }

    suspend fun buildSnapshot(): LocalBackupSnapshot {
        return LocalBackupSnapshot(
            schemaVersion = BACKUP_SCHEMA_VERSION,
            appVersion = resolveAppVersion(),
            exportedAt = System.currentTimeMillis(),
            profile = repository.getProfileSync(),
            exercises = repository.getAllExercises().first(),
            workouts = repository.getAllWorkouts().first(),
            workoutTemplates = repository.getAllTemplates().first(),
            meals = repository.getAllMeals().first(),
            waterEntries = repository.getAllWaterIntake().first(),
            measurements = repository.getAllMeasurements().first(),
            progressPhotos = repository.getAllPhotos().first(),
            personalRecords = repository.getAllRecords().first(),
            dailyStats = repository.getAllDailyStats().first(),
            achievements = repository.getAllAchievements().first(),
            challenges = repository.getAllChallenges().first(),
            meditationSessions = repository.getAllMeditationSessions().first(),
            reminderState = reminderSettingsManager.loadState(),
            savedArticleIds = knowledgeLibraryStateManager.loadSavedArticleIds().toList().sorted(),
            lastReadArticleId = knowledgeLibraryStateManager.loadLastReadArticleId(),
            activeWorkout = activeWorkoutStateManager.loadActiveWorkout(),
            totalXp = xpPrefs.getInt(XPManager.KEY_TOTAL_XP, 0),
            lastLoginDate = xpPrefs.getString(XPManager.KEY_LAST_LOGIN_DATE, null)
        )
    }

    companion object {
        const val BACKUP_SCHEMA_VERSION = CURRENT_BACKUP_SCHEMA_VERSION
        private const val XP_PREFS_NAME = "fitpulse_xp"
    }

    private fun resolveAppVersion(): String {
        return runCatching {
            appContext.packageManager.getPackageInfo(appContext.packageName, 0).versionName
        }.getOrNull() ?: "unknown"
    }
}

private const val MIN_SUPPORTED_BACKUP_SCHEMA_VERSION = 1
private const val CURRENT_BACKUP_SCHEMA_VERSION = 2
