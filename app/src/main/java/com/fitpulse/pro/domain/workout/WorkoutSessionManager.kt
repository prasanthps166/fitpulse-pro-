package com.fitpulse.pro.domain.workout

import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutMood
import com.fitpulse.pro.data.model.WorkoutTemplate
import com.fitpulse.pro.data.preferences.ActiveWorkoutStateManager
import com.fitpulse.pro.data.repository.FitPulseRepository
import com.fitpulse.pro.data.time.CurrentDayMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class WorkoutSessionManager(
    private val repository: FitPulseRepository,
    private val workoutSessionFactory: WorkoutSessionFactory,
    private val currentDayMonitor: CurrentDayMonitor,
    private val activeWorkoutStateManager: ActiveWorkoutStateManager
) {

    val recentWorkouts: Flow<List<Workout>> = repository.getRecentWorkouts(20)
    val totalWorkoutCount: Flow<Int> = repository.getTotalWorkoutCount()
    val totalVolume: Flow<Float?> = repository.getTotalVolume()
    val totalCaloriesBurned: Flow<Int?> = repository.getTotalCaloriesBurned()
    val workoutTemplates: Flow<List<WorkoutTemplate>> = repository.getAllTemplates()

    private val _activeWorkout = MutableStateFlow(activeWorkoutStateManager.loadActiveWorkout())
    val activeWorkout: StateFlow<Workout?> = _activeWorkout

    private val _isWorkoutActive = MutableStateFlow(_activeWorkout.value != null)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive

    private val _lastFinishedWorkout = MutableStateFlow<Workout?>(null)
    val lastFinishedWorkout: StateFlow<Workout?> = _lastFinishedWorkout

    suspend fun startWorkout(name: String = "Workout", templateId: Long? = null) {
        val template = templateId?.let { repository.getTemplateById(it) }
        persistActiveWorkout(
            workoutSessionFactory.createWorkout(name = name, template = template)
        )
    }

    fun addExerciseToWorkout(exercise: Exercise) {
        val updatedWorkout = workoutSessionFactory.addExercise(_activeWorkout.value, exercise)
        _activeWorkout.value = updatedWorkout
        persistActiveWorkout(updatedWorkout)
    }

    fun updateWorkoutExercise(index: Int, workoutExercise: WorkoutExercise) {
        val updatedWorkout = workoutSessionFactory.updateExercise(_activeWorkout.value, index, workoutExercise)
        _activeWorkout.value = updatedWorkout
        persistActiveWorkout(updatedWorkout)
    }

    fun updateWorkoutNotes(notes: String) {
        val updatedWorkout = workoutSessionFactory.updateWorkoutNotes(_activeWorkout.value, notes)
        _activeWorkout.value = updatedWorkout
        persistActiveWorkout(updatedWorkout)
    }

    suspend fun finishWorkout(
        rating: Int,
        mood: WorkoutMood?,
        notes: String
    ): CompletedWorkoutResult? {
        val workout = _activeWorkout.value ?: return null
        val result = workoutSessionFactory.completeWorkout(
            workout = workout,
            rating = rating,
            mood = mood,
            notes = notes
        )
        repository.insertWorkout(result.workout)

        currentDayMonitor.refresh()
        val today = currentDayMonitor.currentDay.value.key
        val currentStats = repository.getStatsForDate(today).first()
        repository.insertOrUpdateStats(
            (currentStats ?: DailyStats(date = today)).copy(
                caloriesBurned = (currentStats?.caloriesBurned ?: 0) + result.caloriesBurned,
                activeMinutes = (currentStats?.activeMinutes ?: 0) + result.durationMinutes,
                workoutCount = (currentStats?.workoutCount ?: 0) + 1,
                totalVolume = (currentStats?.totalVolume ?: 0f) + result.totalVolume
            )
        )

        _lastFinishedWorkout.value = result.workout
        persistActiveWorkout(null)
        return result
    }

    fun cancelWorkout() {
        persistActiveWorkout(null)
    }

    suspend fun getWorkoutById(workoutId: Long): Workout? = repository.getWorkoutById(workoutId)

    suspend fun repeatWorkout(workoutId: Long) {
        val workout = repository.getWorkoutById(workoutId) ?: return
        persistActiveWorkout(workoutSessionFactory.repeatWorkout(workout))
    }

    suspend fun insertTemplate(template: WorkoutTemplate) {
        repository.insertTemplate(template)
    }

    fun clearLastFinishedWorkout() {
        _lastFinishedWorkout.value = null
    }

    fun restorePersistedActiveWorkout() {
        persistActiveWorkout(activeWorkoutStateManager.loadActiveWorkout())
        _lastFinishedWorkout.value = null
    }

    private fun persistActiveWorkout(workout: Workout?) {
        _activeWorkout.value = workout
        _isWorkoutActive.value = workout != null
        activeWorkoutStateManager.saveActiveWorkout(workout)
    }
}
