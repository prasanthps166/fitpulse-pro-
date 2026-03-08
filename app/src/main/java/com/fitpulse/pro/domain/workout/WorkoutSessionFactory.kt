package com.fitpulse.pro.domain.workout

import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.ExerciseSet
import com.fitpulse.pro.data.model.TemplateExercise
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutMood
import com.fitpulse.pro.data.model.WorkoutTemplate

data class CompletedWorkoutResult(
    val workout: Workout,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val totalVolume: Float,
    val personalRecordCount: Int
)

class WorkoutSessionFactory(
    private val calorieBurnRatePerMinute: Float = 6.5f
) {

    fun createWorkout(
        name: String = DEFAULT_WORKOUT_NAME,
        template: WorkoutTemplate? = null,
        startedAtMillis: Long = System.currentTimeMillis()
    ): Workout {
        val exercises = template?.exercises.orEmpty().map { templateExercise ->
            templateExercise.toWorkoutExercise()
        }

        return Workout(
            name = template?.name ?: name,
            startTime = startedAtMillis,
            exercises = exercises
        )
    }

    fun addExercise(workout: Workout?, exercise: Exercise): Workout? {
        val existingExercises = workout?.exercises.orEmpty()
        return workout?.copy(
            exercises = existingExercises + WorkoutExercise(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                sets = listOf(ExerciseSet(setNumber = 1)),
                orderIndex = existingExercises.size
            )
        )
    }

    fun updateExercise(workout: Workout?, index: Int, workoutExercise: WorkoutExercise): Workout? {
        return workout?.copy(
            exercises = workout.exercises.orEmpty().toMutableList().apply {
                if (index in indices) {
                    this[index] = workoutExercise
                }
            }
        )
    }

    fun updateWorkoutNotes(workout: Workout?, notes: String): Workout? {
        return workout?.copy(notes = notes)
    }

    fun repeatWorkout(
        workout: Workout,
        startedAtMillis: Long = System.currentTimeMillis()
    ): Workout {
        return Workout(
            name = workout.name,
            startTime = startedAtMillis,
            exercises = workout.exercises.orEmpty().map { workoutExercise ->
                WorkoutExercise(
                    exerciseId = workoutExercise.exerciseId,
                    exerciseName = workoutExercise.exerciseName,
                    sets = workoutExercise.sets.orEmpty().map { set ->
                        ExerciseSet(
                            setNumber = set.setNumber,
                            reps = set.reps,
                            weightKg = set.weightKg
                        )
                    },
                    restSeconds = workoutExercise.restSeconds,
                    orderIndex = workoutExercise.orderIndex
                )
            }
        )
    }

    fun completeWorkout(
        workout: Workout,
        rating: Int,
        mood: WorkoutMood?,
        notes: String,
        completedAtMillis: Long = System.currentTimeMillis()
    ): CompletedWorkoutResult {
        val durationMinutes = ((completedAtMillis - workout.startTime) / MILLIS_PER_MINUTE).toInt()
        val exercises = workout.exercises.orEmpty()
        val totalVolume = exercises.sumOf { workoutExercise ->
            workoutExercise.sets.orEmpty().sumOf { set ->
                if (set.isCompleted) {
                    (set.weightKg * set.reps).toDouble()
                } else {
                    0.0
                }
            }
        }.toFloat()
        val caloriesBurned = (durationMinutes * calorieBurnRatePerMinute).toInt()
        val personalRecordCount = exercises.sumOf { workoutExercise ->
            workoutExercise.sets.orEmpty().count { it.isPersonalRecord }
        }
        val completedWorkout = workout.copy(
            notes = notes,
            endTime = completedAtMillis,
            durationMinutes = durationMinutes,
            totalCalories = caloriesBurned,
            totalVolume = totalVolume,
            mood = mood,
            rating = rating,
            personalRecordCount = personalRecordCount
        )

        return CompletedWorkoutResult(
            workout = completedWorkout,
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned,
            totalVolume = totalVolume,
            personalRecordCount = personalRecordCount
        )
    }

    private fun TemplateExercise.toWorkoutExercise(): WorkoutExercise {
        return WorkoutExercise(
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            sets = (1..targetSets).map { setNumber ->
                ExerciseSet(
                    setNumber = setNumber,
                    reps = targetReps,
                    weightKg = targetWeightKg
                )
            },
            restSeconds = restSeconds,
            orderIndex = orderIndex
        )
    }

    private companion object {
        const val DEFAULT_WORKOUT_NAME = "Workout"
        const val MILLIS_PER_MINUTE = 60000L
    }
}
