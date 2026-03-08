package com.fitpulse.pro.domain.workout

import com.fitpulse.pro.data.model.ExerciseSet
import com.fitpulse.pro.data.model.TemplateExercise
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutMood
import com.fitpulse.pro.data.model.WorkoutTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class WorkoutSessionFactoryTest {

    private val factory = WorkoutSessionFactory()

    @Test
    fun createWorkout_usesTemplateData() {
        val template = WorkoutTemplate(
            name = "Push Day",
            exercises = listOf(
                TemplateExercise(
                    exerciseId = 10L,
                    exerciseName = "Bench Press",
                    targetSets = 3,
                    targetReps = 8,
                    targetWeightKg = 60f,
                    restSeconds = 90,
                    orderIndex = 0
                )
            )
        )

        val workout = factory.createWorkout(
            name = "Fallback",
            template = template,
            startedAtMillis = 1234L
        )

        assertEquals("Push Day", workout.name)
        assertEquals(1234L, workout.startTime)
        assertEquals(1, workout.exercises?.size)
        assertEquals(3, workout.exercises?.first()?.sets?.size)
        assertEquals(8, workout.exercises?.first()?.sets?.first()?.reps)
        assertEquals(60f, workout.exercises?.first()?.sets?.first()?.weightKg ?: 0f, 0f)
    }

    @Test
    fun completeWorkout_calculatesSummaryValues() {
        val workout = Workout(
            name = "Leg Day",
            startTime = 0L,
            exercises = listOf(
                WorkoutExercise(
                    exerciseId = 1L,
                    exerciseName = "Squat",
                    sets = listOf(
                        ExerciseSet(setNumber = 1, reps = 5, weightKg = 100f, isCompleted = true, isPersonalRecord = true),
                        ExerciseSet(setNumber = 2, reps = 8, weightKg = 80f, isCompleted = true),
                        ExerciseSet(setNumber = 3, reps = 10, weightKg = 60f, isCompleted = false)
                    )
                )
            )
        )

        val result = factory.completeWorkout(
            workout = workout,
            rating = 4,
            mood = WorkoutMood.GOOD,
            notes = "Strong session",
            completedAtMillis = 30L * 60_000L
        )

        assertEquals(30, result.durationMinutes)
        assertEquals(195, result.caloriesBurned)
        assertEquals(1140f, result.totalVolume, 0.001f)
        assertEquals(1, result.personalRecordCount)
        assertEquals("Strong session", result.workout.notes)
        assertEquals(4, result.workout.rating)
        assertEquals(WorkoutMood.GOOD, result.workout.mood)
        assertEquals(30, result.workout.durationMinutes)
        assertEquals(195, result.workout.totalCalories)
        assertEquals(1140f, result.workout.totalVolume, 0.001f)
    }

    @Test
    fun repeatWorkout_keepsLoadDataAndResetsProgressFlags() {
        val original = Workout(
            name = "Upper",
            exercises = listOf(
                WorkoutExercise(
                    exerciseId = 2L,
                    exerciseName = "Row",
                    sets = listOf(
                        ExerciseSet(setNumber = 1, reps = 10, weightKg = 50f, isCompleted = true, isPersonalRecord = true)
                    ),
                    restSeconds = 75,
                    orderIndex = 1
                )
            )
        )

        val repeated = factory.repeatWorkout(original, startedAtMillis = 5000L)

        assertEquals("Upper", repeated.name)
        assertEquals(5000L, repeated.startTime)
        assertNull(repeated.endTime)
        assertEquals(10, repeated.exercises?.first()?.sets?.first()?.reps)
        assertEquals(50f, repeated.exercises?.first()?.sets?.first()?.weightKg ?: 0f, 0f)
        assertFalse(repeated.exercises?.first()?.sets?.first()?.isCompleted ?: true)
        assertFalse(repeated.exercises?.first()?.sets?.first()?.isPersonalRecord ?: true)
    }
}
