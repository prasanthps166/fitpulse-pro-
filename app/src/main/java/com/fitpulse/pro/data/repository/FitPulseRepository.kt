package com.fitpulse.pro.data.repository

import com.fitpulse.pro.data.local.FitPulseDatabase
import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.BodyMeasurement
import com.fitpulse.pro.data.model.Challenge
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.ExerciseCategory
import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.MeditationSession
import com.fitpulse.pro.data.model.MuscleGroup
import com.fitpulse.pro.data.model.PersonalRecord
import com.fitpulse.pro.data.model.PhotoCategory
import com.fitpulse.pro.data.model.ProgressPhoto
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.data.model.WaterIntake
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutTemplate
import com.fitpulse.pro.data.model.DailyStats
import kotlinx.coroutines.flow.Flow

class FitPulseRepository(private val database: FitPulseDatabase) {

    fun getProfile(): Flow<UserProfile?> = database.userProfileDao().getProfile()
    suspend fun getProfileSync(): UserProfile? = database.userProfileDao().getProfileSync()
    suspend fun saveProfile(profile: UserProfile) = database.userProfileDao().insertOrUpdate(profile)
    suspend fun completeOnboarding() = database.userProfileDao().completeOnboarding()

    fun getAllExercises(): Flow<List<Exercise>> = database.exerciseDao().getAllExercises()
    fun getCustomExercises(): Flow<List<Exercise>> = database.exerciseDao().getCustomExercises()
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>> =
        database.exerciseDao().getExercisesByCategory(category)
    fun getExercisesByMuscleGroup(muscleGroup: MuscleGroup): Flow<List<Exercise>> =
        database.exerciseDao().getExercisesByMuscleGroup(muscleGroup)
    fun searchExercises(query: String): Flow<List<Exercise>> = database.exerciseDao().searchExercises(query)
    suspend fun getExerciseById(id: Long): Exercise? = database.exerciseDao().getExerciseById(id)
    suspend fun insertExercise(exercise: Exercise): Long = database.exerciseDao().insertExercise(exercise)
    suspend fun insertAllExercises(exercises: List<Exercise>) = database.exerciseDao().insertAll(exercises)
    suspend fun deleteExercise(exercise: Exercise) = database.exerciseDao().deleteExercise(exercise)
    suspend fun getExerciseCount(): Int = database.exerciseDao().getExerciseCount()

    fun getAllWorkouts(): Flow<List<Workout>> = database.workoutDao().getAllWorkouts()
    suspend fun getAllWorkoutsSync(): List<Workout> = database.workoutDao().getAllWorkoutsSync()
    fun getRecentWorkouts(limit: Int = 10): Flow<List<Workout>> = database.workoutDao().getRecentWorkouts(limit)
    suspend fun getWorkoutById(id: Long): Workout? = database.workoutDao().getWorkoutById(id)
    fun getWorkoutsBetweenDates(startDate: Long, endDate: Long): Flow<List<Workout>> =
        database.workoutDao().getWorkoutsBetweenDates(startDate, endDate)
    suspend fun getWorkoutsBetweenDatesSync(startDate: Long, endDate: Long): List<Workout> =
        database.workoutDao().getWorkoutsBetweenDatesSync(startDate, endDate)
    fun getTotalWorkoutCount(): Flow<Int> = database.workoutDao().getTotalWorkoutCount()
    fun getTotalVolume(): Flow<Float?> = database.workoutDao().getTotalVolume()
    fun getTotalCaloriesBurned(): Flow<Int?> = database.workoutDao().getTotalCaloriesBurned()
    suspend fun insertWorkout(workout: Workout): Long = database.workoutDao().insertWorkout(workout)
    suspend fun updateWorkout(workout: Workout) = database.workoutDao().updateWorkout(workout)
    suspend fun deleteWorkout(workout: Workout) = database.workoutDao().deleteWorkout(workout)

    fun getAllTemplates(): Flow<List<WorkoutTemplate>> = database.workoutTemplateDao().getAllTemplates()
    fun getPresetTemplates(): Flow<List<WorkoutTemplate>> = database.workoutTemplateDao().getPresetTemplates()
    fun getCustomTemplates(): Flow<List<WorkoutTemplate>> = database.workoutTemplateDao().getCustomTemplates()
    suspend fun getTemplateById(id: Long): WorkoutTemplate? = database.workoutTemplateDao().getTemplateById(id)
    suspend fun insertTemplate(template: WorkoutTemplate): Long = database.workoutTemplateDao().insertTemplate(template)
    suspend fun insertAllTemplates(templates: List<WorkoutTemplate>) = database.workoutTemplateDao().insertAll(templates)
    suspend fun getTemplateCount(): Int = database.workoutTemplateDao().getTemplateCount()

    fun getAllMeals(): Flow<List<MealEntry>> = database.mealEntryDao().getAllMeals()
    fun getMealsByDate(startOfDay: Long, endOfDay: Long): Flow<List<MealEntry>> =
        database.mealEntryDao().getMealsByDate(startOfDay, endOfDay)
    fun getTotalCaloriesForDay(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        database.mealEntryDao().getTotalCaloriesForDay(startOfDay, endOfDay)
    fun getTotalProteinForDay(startOfDay: Long, endOfDay: Long): Flow<Float?> =
        database.mealEntryDao().getTotalProteinForDay(startOfDay, endOfDay)
    fun getTotalCarbsForDay(startOfDay: Long, endOfDay: Long): Flow<Float?> =
        database.mealEntryDao().getTotalCarbsForDay(startOfDay, endOfDay)
    fun getTotalFatForDay(startOfDay: Long, endOfDay: Long): Flow<Float?> =
        database.mealEntryDao().getTotalFatForDay(startOfDay, endOfDay)
    suspend fun insertMeal(meal: MealEntry): Long = database.mealEntryDao().insertMeal(meal)
    suspend fun updateMeal(meal: MealEntry) = database.mealEntryDao().updateMeal(meal)
    suspend fun deleteMeal(meal: MealEntry) = database.mealEntryDao().deleteMeal(meal)

    fun getAllWaterIntake(): Flow<List<WaterIntake>> = database.waterIntakeDao().getAllWaterIntake()
    fun getWaterIntakeByDate(startOfDay: Long, endOfDay: Long): Flow<List<WaterIntake>> =
        database.waterIntakeDao().getWaterIntakeByDate(startOfDay, endOfDay)
    fun getTotalWaterForDay(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        database.waterIntakeDao().getTotalWaterForDay(startOfDay, endOfDay)
    suspend fun getLatestWaterForDay(startOfDay: Long, endOfDay: Long): WaterIntake? =
        database.waterIntakeDao().getLatestWaterForDay(startOfDay, endOfDay)
    suspend fun insertWater(water: WaterIntake): Long = database.waterIntakeDao().insertWater(water)
    suspend fun deleteWaterBetweenDates(startOfDay: Long, endOfDay: Long): Int =
        database.waterIntakeDao().deleteWaterBetweenDates(startOfDay, endOfDay)
    suspend fun deleteWater(water: WaterIntake) = database.waterIntakeDao().deleteWater(water)

    fun getAllMeasurements(): Flow<List<BodyMeasurement>> = database.bodyMeasurementDao().getAllMeasurements()
    fun getLatestMeasurement(): Flow<BodyMeasurement?> = database.bodyMeasurementDao().getLatestMeasurement()
    fun getRecentMeasurements(limit: Int): Flow<List<BodyMeasurement>> =
        database.bodyMeasurementDao().getRecentMeasurements(limit)
    suspend fun deleteMeasurement(measurement: BodyMeasurement) = database.bodyMeasurementDao().deleteMeasurement(measurement)
    suspend fun insertMeasurement(measurement: BodyMeasurement): Long =
        database.bodyMeasurementDao().insertMeasurement(measurement)

    fun getAllPhotos(): Flow<List<ProgressPhoto>> = database.progressPhotoDao().getAllPhotos()
    fun getPhotosByCategory(category: PhotoCategory): Flow<List<ProgressPhoto>> =
        database.progressPhotoDao().getPhotosByCategory(category)
    suspend fun insertPhoto(photo: ProgressPhoto): Long = database.progressPhotoDao().insertPhoto(photo)

    fun getAllRecords(): Flow<List<PersonalRecord>> = database.personalRecordDao().getAllRecords()
    fun getRecordsForExercise(exerciseId: Long): Flow<List<PersonalRecord>> =
        database.personalRecordDao().getRecordsForExercise(exerciseId)
    fun getRecentRecords(limit: Int): Flow<List<PersonalRecord>> =
        database.personalRecordDao().getRecentRecords(limit)
    suspend fun insertRecord(record: PersonalRecord): Long = database.personalRecordDao().insertRecord(record)

    fun getAllDailyStats(): Flow<List<DailyStats>> = database.dailyStatsDao().getAllStats()
    fun getStatsForDate(date: String): Flow<DailyStats?> = database.dailyStatsDao().getStatsForDate(date)
    fun getRecentStats(days: Int = 7): Flow<List<DailyStats>> = database.dailyStatsDao().getRecentStats(days)
    fun getStatsBetweenDates(startDate: String, endDate: String): Flow<List<DailyStats>> =
        database.dailyStatsDao().getStatsBetweenDates(startDate, endDate)
    fun getMaxStreak(): Flow<Int?> = database.dailyStatsDao().getMaxStreak()
    suspend fun insertOrUpdateStats(stats: DailyStats) = database.dailyStatsDao().insertOrUpdate(stats)

    fun getAllAchievements(): Flow<List<Achievement>> = database.achievementDao().getAllAchievements()
    fun getUnlockedAchievements(): Flow<List<Achievement>> = database.achievementDao().getUnlockedAchievements()
    fun getUnlockedCount(): Flow<Int> = database.achievementDao().getUnlockedCount()
    suspend fun getAchievementCount(): Int = database.achievementDao().getAchievementCount()
    suspend fun insertAchievement(achievement: Achievement) = database.achievementDao().insertAchievement(achievement)
    suspend fun insertAllAchievements(achievements: List<Achievement>) = database.achievementDao().insertAll(achievements)
    suspend fun updateAchievement(achievement: Achievement) = database.achievementDao().updateAchievement(achievement)

    fun getAllChallenges(): Flow<List<Challenge>> = database.challengeDao().getAllChallenges()
    fun getActiveChallenges(): Flow<List<Challenge>> = database.challengeDao().getActiveChallenges()
    fun getCompletedChallenges(): Flow<List<Challenge>> = database.challengeDao().getCompletedChallenges()
    suspend fun insertChallenge(challenge: Challenge): Long = database.challengeDao().insertChallenge(challenge)
    suspend fun updateChallenge(challenge: Challenge) = database.challengeDao().updateChallenge(challenge)

    fun getAllMeditationSessions(): Flow<List<MeditationSession>> = database.meditationSessionDao().getAllSessions()
    fun getRecentMeditationSessions(limit: Int): Flow<List<MeditationSession>> =
        database.meditationSessionDao().getRecentSessions(limit)
    fun getTotalMeditationMinutes(): Flow<Int?> = database.meditationSessionDao().getTotalMinutes()
    suspend fun insertMeditationSession(session: MeditationSession): Long =
        database.meditationSessionDao().insertSession(session)

    suspend fun clearAllData() {
        database.clearAllTables()
    }
}
