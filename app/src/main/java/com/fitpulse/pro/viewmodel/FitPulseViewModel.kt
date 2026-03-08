package com.fitpulse.pro.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitpulse.pro.FitPulseApp
import com.fitpulse.pro.data.content.ArticleCatalog
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.data.preferences.KnowledgeLibraryStateManager
import com.fitpulse.pro.data.preferences.ReminderSettingsManager
import com.fitpulse.pro.data.time.CurrentDayMonitor
import com.fitpulse.pro.data.preferences.ReminderState
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.domain.achievements.AchievementManager
import com.fitpulse.pro.domain.home.CoachTipGenerator
import com.fitpulse.pro.domain.nutrition.NutritionManager
import com.fitpulse.pro.domain.progress.PRDetectionResult
import com.fitpulse.pro.domain.progress.PersonalRecordManager
import com.fitpulse.pro.domain.progress.ProgressManager
import com.fitpulse.pro.domain.progress.WorkoutStreakManager
import com.fitpulse.pro.domain.workout.WorkoutSessionManager
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.utils.WorkoutShareHelper
import com.fitpulse.pro.utils.XPGainResult
import com.fitpulse.pro.utils.XPManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FitPulseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitPulseRepository = (application as FitPulseApp).repository
    private val reminderSettingsManager: ReminderSettingsManager =
        (application as FitPulseApp).reminderSettingsManager
    private val knowledgeLibraryStateManager: KnowledgeLibraryStateManager =
        (application as FitPulseApp).knowledgeLibraryStateManager
    private val progressManager: ProgressManager =
        (application as FitPulseApp).progressManager
    private val achievementManager: AchievementManager =
        (application as FitPulseApp).achievementManager
    private val coachTipGenerator: CoachTipGenerator =
        (application as FitPulseApp).coachTipGenerator
    private val workoutStreakManager: WorkoutStreakManager =
        (application as FitPulseApp).workoutStreakManager
    private val personalRecordManager: PersonalRecordManager =
        (application as FitPulseApp).personalRecordManager
    private val workoutSessionManager: WorkoutSessionManager =
        (application as FitPulseApp).workoutSessionManager
    private val nutritionManager: NutritionManager =
        (application as FitPulseApp).nutritionManager
    private val currentDayMonitor: CurrentDayMonitor =
        (application as FitPulseApp).currentDayMonitor
    private val articleCatalog: ArticleCatalog =
        (application as FitPulseApp).articleCatalog
    val xpManager = XPManager(application)

    // ========== XP & Level ==========
    private val _totalXP = MutableStateFlow(0)
    val totalXP: StateFlow<Int> = _totalXP
    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel
    private val _xpProgress = MutableStateFlow(0f)
    val xpProgress: StateFlow<Float> = _xpProgress
    private val _lastXPGain = MutableStateFlow<XPGainResult?>(null)
    val lastXPGain: StateFlow<XPGainResult?> = _lastXPGain
    private val _reminderState = MutableStateFlow(ReminderState())
    val reminderState: StateFlow<ReminderState> = _reminderState
    private val _savedArticleIds = MutableStateFlow(emptySet<String>())
    val savedArticleIds: StateFlow<Set<String>> = _savedArticleIds
    private val _lastReadArticleId = MutableStateFlow<String?>(null)
    val lastReadArticleId: StateFlow<String?> = _lastReadArticleId

    init {
        refreshXP()
        loadReminderState()
        loadKnowledgeLibraryState()
        xpManager.checkDailyLogin()?.let { result ->
            _lastXPGain.value = result
            refreshXP()
        }
        viewModelScope.launch {
            syncAchievements()
        }
    }

    private fun refreshXP() {
        _totalXP.value = xpManager.getTotalXP()
        _currentLevel.value = xpManager.getLevel()
        _xpProgress.value = xpManager.getXPProgress()
    }

    private fun awardXP(amount: Int) {
        val result = xpManager.addXP(amount)
        _lastXPGain.value = result
        refreshXP()
    }

    fun clearLastXPGain() {
        _lastXPGain.value = null
    }

    // ========== User Profile ==========
    val userProfile: StateFlow<UserProfile?> = repository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
        }
    }

    fun updateUserProfile(name: String, email: String) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile != null) {
                repository.saveProfile(currentProfile.copy(name = name, email = email))
            } else {
                repository.saveProfile(UserProfile(name = name, email = email))
            }
        }
    }

    fun updateAvatar(uri: String) {
        viewModelScope.launch {
            userProfile.value?.let { profile ->
                repository.saveProfile(profile.copy(avatarUri = uri))
            }
        }
    }

    // ========== Exercises ==========
    val allExercises: StateFlow<List<Exercise>> = repository.getAllExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _exerciseSearchQuery = MutableStateFlow("")
    val exerciseSearchQuery: StateFlow<String> = _exerciseSearchQuery

    val filteredExercises: StateFlow<List<Exercise>> = combine(
        allExercises,
        _exerciseSearchQuery
    ) { exercises, query ->
        if (query.isBlank()) {
            exercises
        } else {
            exercises.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.muscleGroup.name.contains(query, ignoreCase = true) ||
                    it.category.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setExerciseSearchQuery(query: String) {
        _exerciseSearchQuery.value = query
    }

    fun getExercisesByCategory(category: ExerciseCategory): StateFlow<List<Exercise>> =
        repository.getExercisesByCategory(category)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): StateFlow<List<Exercise>> =
        repository.getExercisesByMuscleGroup(muscleGroup)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customExercises: StateFlow<List<Exercise>> = repository.getCustomExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.insertExercise(exercise.copy(isCustom = true))
            awardXP(XPManager.XP_MEAL_LOG)
        }
    }

    fun deleteCustomExercise(exercise: Exercise) {
        viewModelScope.launch {
            if (exercise.isCustom) {
                repository.deleteExercise(exercise)
            }
        }
    }

    // ========== Workouts ==========
    val recentWorkouts: StateFlow<List<Workout>> = workoutSessionManager.recentWorkouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWorkoutCount: StateFlow<Int> = workoutSessionManager.totalWorkoutCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalVolume: StateFlow<Float?> = workoutSessionManager.totalVolume
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalCaloriesBurned: StateFlow<Int?> = workoutSessionManager.totalCaloriesBurned
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeWorkout: StateFlow<Workout?> = workoutSessionManager.activeWorkout
    val isWorkoutActive: StateFlow<Boolean> = workoutSessionManager.isWorkoutActive

    fun startWorkout(name: String = "Workout", templateId: Long? = null) {
        viewModelScope.launch {
            workoutSessionManager.startWorkout(name = name, templateId = templateId)
        }
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        workoutSessionManager.addExerciseToWorkout(exercise)
    }

    fun updateWorkoutExercise(index: Int, workoutExercise: WorkoutExercise) {
        workoutSessionManager.updateWorkoutExercise(index, workoutExercise)
    }

    fun updateActiveWorkoutNotes(notes: String) {
        workoutSessionManager.updateWorkoutNotes(notes)
    }

    fun finishWorkout(rating: Int, mood: WorkoutMood? = null, notes: String = "") {
        viewModelScope.launch {
            val result = workoutSessionManager.finishWorkout(rating, mood, notes) ?: return@launch
            val streakUpdate = workoutStreakManager.updateWorkoutStreak()
            if (streakUpdate.shouldAwardBonusXp) {
                awardXP(XPManager.XP_STREAK_DAY)
            }
            awardXP(XPManager.XP_WORKOUT_COMPLETE)

            val completionTime = result.workout.endTime ?: System.currentTimeMillis()
            val hourOfDay = java.util.Calendar.getInstance().apply {
                timeInMillis = completionTime
            }.get(java.util.Calendar.HOUR_OF_DAY)
            if (hourOfDay < 7) {
                unlockSpecialAchievement("early_bird")
            }
            if (hourOfDay >= 22) {
                unlockSpecialAchievement("night_owl")
            }

            syncAchievements()
        }
    }

    fun cancelWorkout() {
        workoutSessionManager.cancelWorkout()
    }

    suspend fun getWorkoutById(workoutId: Long): Workout? = workoutSessionManager.getWorkoutById(workoutId)

    fun repeatWorkout(workoutId: Long) {
        viewModelScope.launch {
            workoutSessionManager.repeatWorkout(workoutId)
        }
    }

    // ========== Workout Templates ==========
    val workoutTemplates: StateFlow<List<WorkoutTemplate>> = workoutSessionManager.workoutTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            workoutSessionManager.insertTemplate(template)
        }
    }

    // ========== Nutrition ==========
    val todayMeals: StateFlow<List<MealEntry>> = nutritionManager.todayMeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCalories: StateFlow<Int?> = nutritionManager.todayCalories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayProtein: StateFlow<Float?> = nutritionManager.todayProtein
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayCarbs: StateFlow<Float?> = nutritionManager.todayCarbs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayFat: StateFlow<Float?> = nutritionManager.todayFat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayWater: StateFlow<Int?> = nutritionManager.todayWater
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayWaterEntries: StateFlow<List<WaterIntake>> = nutritionManager.todayWaterEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyMeals: StateFlow<List<MealEntry>> = nutritionManager.weeklyMeals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyWaterEntries: StateFlow<List<WaterIntake>> = nutritionManager.weeklyWaterEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun logMeal(meal: MealEntry) {
        viewModelScope.launch {
            nutritionManager.logMeal(meal)
            awardXP(XPManager.XP_MEAL_LOG)
            syncAchievements()
        }
    }

    fun deleteMeal(meal: MealEntry) {
        viewModelScope.launch {
            nutritionManager.deleteMeal(meal)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            nutritionManager.addWater(amountMl)
            awardXP(XPManager.XP_WATER_LOG)
            syncAchievements()
        }
    }

    fun undoLastWater() {
        viewModelScope.launch {
            nutritionManager.undoLastWater()
        }
    }

    fun clearTodayWater() {
        viewModelScope.launch {
            nutritionManager.clearTodayWater()
        }
    }

    // ========== Body Measurements ==========
    val latestMeasurement: StateFlow<BodyMeasurement?> = progressManager.latestMeasurement
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recentMeasurements: StateFlow<List<BodyMeasurement>> = progressManager.recentMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun logMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch {
            progressManager.logMeasurement(measurement)
            awardXP(XPManager.XP_MEAL_LOG) // Small XP for logging measurements
        }
    }

    // All measurements for weight chart
    val allMeasurements: StateFlow<List<BodyMeasurement>> = progressManager.allMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteMeasurement(measurement: BodyMeasurement) {
        viewModelScope.launch { progressManager.deleteMeasurement(measurement) }
    }

    // ========== Progress Photos ==========
    val progressPhotos: StateFlow<List<ProgressPhoto>> = progressManager.progressPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addProgressPhoto(photo: ProgressPhoto) {
        viewModelScope.launch { progressManager.addProgressPhoto(photo) }
    }

    // ========== Personal Records ==========
    val personalRecords: StateFlow<List<PersonalRecord>> = progressManager.personalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPersonalRecord(record: PersonalRecord) {
        viewModelScope.launch { progressManager.addPersonalRecord(record) }
    }

    // ========== Daily Stats ==========
    val todayStats: StateFlow<DailyStats?> = progressManager.todayStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weeklyStats: StateFlow<List<DailyStats>> = progressManager.weeklyStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maxStreak: StateFlow<Int?> = progressManager.maxStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val coachTips: StateFlow<List<CoachTip>> = combine(todayStats, userProfile) { stats, profile ->
        coachTipGenerator.generate(stats, profile)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        coachTipGenerator.generate(null, null)
    )

    // ========== Calendar Data ==========
    private val _calendarWorkoutData = MutableStateFlow<Map<String, Int>>(emptyMap())
    val calendarWorkoutData: StateFlow<Map<String, Int>> = _calendarWorkoutData

    fun loadCalendarData(monthOffset: Int = 0) {
        viewModelScope.launch {
            _calendarWorkoutData.value = progressManager.loadCalendarData(monthOffset)
        }
    }

    fun updateSteps(steps: Int) {
        viewModelScope.launch {
            progressManager.updateSteps(steps)
        }
    }

    // ========== Achievements ==========
    val achievements: StateFlow<List<Achievement>> = achievementManager.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unlockedAchievementCount: StateFlow<Int> = achievementManager.unlockedAchievementCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ========== Challenges ==========
    val activeChallenges: StateFlow<List<Challenge>> = repository.getActiveChallenges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createChallenge(challenge: Challenge) {
        viewModelScope.launch { repository.insertChallenge(challenge) }
    }

    // ========== Meditation ==========
    val recentMeditations: StateFlow<List<MeditationSession>> = repository.getRecentMeditationSessions(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalMeditationMinutes: StateFlow<Int?> = repository.getTotalMeditationMinutes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logMeditation(session: MeditationSession) {
        viewModelScope.launch {
            repository.insertMeditationSession(session)
            awardXP(XPManager.XP_MEDITATION)
            syncAchievements()
        }
    }
    // ========== PR Auto-Detection ==========
    val newPRDetected: StateFlow<PRDetectionResult?> = personalRecordManager.newPRDetected

    fun clearNewPR() {
        personalRecordManager.clearNewPR()
    }

    /**
     * Called when a set is completed. Checks if this set beats the current 1RM
     * for the given exercise. If so, saves the new PR and fires the event.
     */
    fun detectAndStorePR(
        exerciseId: Long,
        exerciseName: String,
        weightKg: Float,
        reps: Int
    ) {
        viewModelScope.launch {
            val detectionResult = personalRecordManager.detectAndStorePR(
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                weightKg = weightKg,
                reps = reps
            )
            if (detectionResult != null) {
                awardXP(XPManager.XP_PERSONAL_RECORD)
                syncAchievements()
            }
        }
    }

    // ========== Sleep Tracking ==========
    fun logSleep(sleepHours: Float, sleepQuality: Int) {
        viewModelScope.launch {
            currentDayMonitor.refresh()
            val today = currentDayMonitor.currentDay.value.key
            val currentStats = repository.getStatsForDate(today).first()
            repository.insertOrUpdateStats(
                (currentStats ?: DailyStats(date = today)).copy(
                    sleepHours = sleepHours,
                    sleepQuality = sleepQuality
                )
            )
            awardXP(10) // small XP for logging sleep
        }
    }

    // ========== Last Finished Workout (for sharing) ==========
    val lastFinishedWorkout: StateFlow<Workout?> = workoutSessionManager.lastFinishedWorkout

    fun shareWorkout(context: android.content.Context) {
        val workout = lastFinishedWorkout.value ?: return
        val profile = userProfile.value
        WorkoutShareHelper.shareWorkoutCard(
            context = context,
            workoutName = workout.name,
            duration = Utils.formatDuration(workout.durationMinutes),
            exercises = (workout.exercises ?: emptyList()).size,
            totalVolume = Utils.formatWeight(workout.totalVolume),
            calories = workout.totalCalories,
            rating = workout.rating,
            userName = profile?.name?.ifBlank { "Athlete" } ?: "Athlete",
            level = xpManager.getLevel(),
            rank = xpManager.getRank()
        )
    }

    fun clearLastFinishedWorkout() {
        workoutSessionManager.clearLastFinishedWorkout()
    }

    fun loadReminderState() {
        _reminderState.value = reminderSettingsManager.loadState()
    }

    fun loadKnowledgeLibraryState() {
        _savedArticleIds.value = knowledgeLibraryStateManager.loadSavedArticleIds()
        _lastReadArticleId.value = knowledgeLibraryStateManager.loadLastReadArticleId()
    }

    fun setWorkoutReminder(hour: Int, minute: Int) {
        _reminderState.value = reminderSettingsManager.setWorkoutReminder(hour, minute)
    }

    fun cancelWorkoutReminder() {
        _reminderState.value = reminderSettingsManager.cancelWorkoutReminder()
    }

    fun setWaterReminders(enabled: Boolean) {
        _reminderState.value = reminderSettingsManager.setWaterReminders(enabled)
    }

    // ========== Articles ==========
    val articles: List<FitnessArticle> = articleCatalog.articles

    fun getArticleById(id: String): FitnessArticle? = articleCatalog.getById(id)

    fun toggleSavedArticle(articleId: String) {
        val currentSaved = _savedArticleIds.value
        val updated = if (articleId in currentSaved) {
            currentSaved - articleId
        } else {
            currentSaved + articleId
        }
        _savedArticleIds.value = knowledgeLibraryStateManager.setSavedArticleIds(updated)
    }

    fun markArticleRead(articleId: String) {
        _lastReadArticleId.value = knowledgeLibraryStateManager.setLastReadArticleId(articleId)
    }

    private suspend fun syncAchievements() {
        runCatching {
            achievementManager.syncProgress().forEach { achievement ->
                awardXP(achievement.xpReward)
            }
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to sync achievements", throwable)
        }
    }

    private suspend fun unlockSpecialAchievement(achievementId: String) {
        runCatching {
            val achievement = achievementManager.unlockSpecialAchievement(achievementId) ?: return
            awardXP(achievement.xpReward)
        }.onFailure { throwable ->
            Log.e(TAG, "Failed to unlock special achievement: ", throwable)
        }
    }

    // ========== Clear All Data ==========
    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            workoutSessionManager.cancelWorkout()
            _reminderState.value = reminderSettingsManager.clearAll()
            knowledgeLibraryStateManager.clearAll()
            val prefs = getApplication<Application>().getSharedPreferences(
                "fitpulse_xp",
                android.content.Context.MODE_PRIVATE
            )
            prefs.edit().clear().apply()
            val app = getApplication<Application>() as FitPulseApp
            repository.insertAllExercises(app.seedCatalog.exercises())
            repository.insertAllTemplates(app.seedCatalog.workoutTemplates())
            repository.insertAllAchievements(app.seedCatalog.achievements())
            refreshXP()
        }
    }

    suspend fun exportLocalBackup(uri: Uri) =
        (getApplication<Application>() as FitPulseApp).localBackupManager.exportTo(uri)

    suspend fun importLocalBackup(uri: Uri) =
        (getApplication<Application>() as FitPulseApp).localBackupManager.importFrom(uri).also {
            workoutSessionManager.restorePersistedActiveWorkout()
            loadReminderState()
            loadKnowledgeLibraryState()
            refreshXP()
        }

    private companion object {
        const val TAG = "FitPulseViewModel"
    }

    // Helper for navigation reference
    private object Screen {
        val Workouts = com.fitpulse.pro.navigation.Screen.Workouts
    }
}










