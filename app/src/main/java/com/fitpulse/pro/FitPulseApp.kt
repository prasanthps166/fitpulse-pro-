package com.fitpulse.pro

import android.app.Application
import android.util.Log
import com.fitpulse.pro.data.backup.LocalBackupManager
import com.fitpulse.pro.data.content.ArticleCatalog
import com.fitpulse.pro.data.local.FitPulseDatabase
import com.fitpulse.pro.data.preferences.ActiveWorkoutStateManager
import com.fitpulse.pro.data.preferences.KnowledgeLibraryStateManager
import com.fitpulse.pro.data.preferences.ReminderSettingsManager
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.data.seed.SeedCatalog
import com.fitpulse.pro.data.time.AppDateProvider
import com.fitpulse.pro.data.time.CurrentDayMonitor
import com.fitpulse.pro.domain.achievements.AchievementManager
import com.fitpulse.pro.domain.achievements.AchievementProgressEvaluator
import com.fitpulse.pro.domain.home.CoachTipGenerator
import com.fitpulse.pro.domain.nutrition.NutritionManager
import com.fitpulse.pro.domain.nutrition.NutritionStatsMutator
import com.fitpulse.pro.domain.progress.CalendarWorkoutDataBuilder
import com.fitpulse.pro.domain.progress.PersonalRecordDetector
import com.fitpulse.pro.domain.progress.PersonalRecordManager
import com.fitpulse.pro.domain.progress.ProgressManager
import com.fitpulse.pro.domain.progress.WorkoutStreakCalculator
import com.fitpulse.pro.domain.progress.WorkoutStreakManager
import com.fitpulse.pro.domain.workout.WorkoutSessionFactory
import com.fitpulse.pro.domain.workout.WorkoutSessionManager
import com.fitpulse.pro.ui.theme.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FitPulseApp : Application() {

    lateinit var database: FitPulseDatabase
        private set
    lateinit var repository: FitPulseRepository
        private set
    lateinit var dateProvider: AppDateProvider
        private set
    lateinit var currentDayMonitor: CurrentDayMonitor
        private set
    lateinit var seedCatalog: SeedCatalog
        private set
    lateinit var articleCatalog: ArticleCatalog
        private set
    lateinit var knowledgeLibraryStateManager: KnowledgeLibraryStateManager
        private set
    lateinit var reminderSettingsManager: ReminderSettingsManager
        private set
    lateinit var activeWorkoutStateManager: ActiveWorkoutStateManager
        private set
    lateinit var localBackupManager: LocalBackupManager
        private set
    lateinit var progressManager: ProgressManager
        private set
    lateinit var achievementManager: AchievementManager
        private set
    lateinit var coachTipGenerator: CoachTipGenerator
        private set
    lateinit var workoutStreakManager: WorkoutStreakManager
        private set
    lateinit var personalRecordManager: PersonalRecordManager
        private set
    lateinit var workoutSessionManager: WorkoutSessionManager
        private set
    lateinit var nutritionManager: NutritionManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        ThemeManager.initialize(this)

        database = initializeDatabase()
        repository = FitPulseRepository(database)
        dateProvider = AppDateProvider()
        currentDayMonitor = CurrentDayMonitor(applicationScope, dateProvider)
        seedCatalog = SeedCatalog()
        articleCatalog = ArticleCatalog()
        knowledgeLibraryStateManager = KnowledgeLibraryStateManager(this)
        reminderSettingsManager = ReminderSettingsManager(this)
        activeWorkoutStateManager = ActiveWorkoutStateManager(this)
        progressManager = ProgressManager(repository, CalendarWorkoutDataBuilder(), currentDayMonitor)
        achievementManager = AchievementManager(repository, AchievementProgressEvaluator())
        coachTipGenerator = CoachTipGenerator()
        workoutStreakManager = WorkoutStreakManager(repository, WorkoutStreakCalculator(), currentDayMonitor)
        personalRecordManager = PersonalRecordManager(repository, PersonalRecordDetector())
        workoutSessionManager = WorkoutSessionManager(
            repository = repository,
            workoutSessionFactory = WorkoutSessionFactory(),
            currentDayMonitor = currentDayMonitor,
            activeWorkoutStateManager = activeWorkoutStateManager
        )
        nutritionManager = NutritionManager(repository, NutritionStatsMutator(), currentDayMonitor)
        localBackupManager = LocalBackupManager(
            context = this,
            database = database,
            repository = repository,
            reminderSettingsManager = reminderSettingsManager,
            knowledgeLibraryStateManager = knowledgeLibraryStateManager,
            activeWorkoutStateManager = activeWorkoutStateManager
        )

        applicationScope.launch {
            runCatching {
                seedDataIfNeeded()
            }.onFailure { throwable ->
                Log.e(TAG, "Failed to seed initial data", throwable)
            }
        }
    }

    private fun initializeDatabase(): FitPulseDatabase {
        return runCatching {
            FitPulseDatabase.getDatabase(this)
        }.getOrElse { throwable ->
            Log.e(TAG, "Failed to open database, recreating local storage", throwable)
            FitPulseDatabase.resetInstance()
            deleteDatabase(com.fitpulse.pro.data.local.FITPULSE_DATABASE_NAME)
            FitPulseDatabase.getDatabase(this)
        }
    }

    private suspend fun seedDataIfNeeded() {
        if (repository.getExerciseCount() == 0) {
            repository.insertAllExercises(seedCatalog.exercises())
        }

        if (repository.getTemplateCount() == 0) {
            repository.insertAllTemplates(seedCatalog.workoutTemplates())
        }

        if (repository.getAchievementCount() == 0) {
            repository.insertAllAchievements(seedCatalog.achievements())
        }
    }

    companion object {
        private const val TAG = "FitPulseApp"

        lateinit var instance: FitPulseApp
            private set
    }
}

