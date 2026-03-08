package com.fitpulse.pro.ui.screens.workout

import com.fitpulse.pro.data.model.Difficulty
import com.fitpulse.pro.data.model.Equipment
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.ExerciseCategory
import com.fitpulse.pro.data.model.ExerciseSet
import com.fitpulse.pro.data.model.MuscleGroup
import com.fitpulse.pro.data.model.TemplateExercise
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutTemplate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkoutExperienceTest {

    @Test
    fun buildQuickStartTemplates_prioritizesCommonStarterPaths() {
        val templates = listOf(
            template(name = "General Fitness Express", difficulty = Difficulty.BEGINNER),
            template(name = "Random Split", difficulty = Difficulty.ADVANCED),
            template(name = "Full Body Starter", difficulty = Difficulty.BEGINNER),
            template(name = "StrongLifts 5x5 A", difficulty = Difficulty.BEGINNER),
            template(name = "Full Body Progression", difficulty = Difficulty.INTERMEDIATE)
        )

        val ordered = buildQuickStartTemplates(templates)

        assertEquals("Full Body Starter", ordered.first().name)
        assertEquals("General Fitness Express", ordered[1].name)
        assertEquals("StrongLifts 5x5 A", ordered[2].name)
        assertTrue(ordered.last().name == "Random Split")
    }

    @Test
    fun buildPreviousExercisePerformanceMap_usesMostRecentCompletedWorkingSet() {
        val workouts = listOf(
            Workout(
                id = 1,
                name = "Older Session",
                createdAt = 100L,
                exercises = listOf(
                    WorkoutExercise(
                        exerciseId = 41,
                        exerciseName = "Barbell Squat",
                        sets = listOf(
                            ExerciseSet(setNumber = 1, reps = 5, weightKg = 80f, isCompleted = true)
                        )
                    )
                )
            ),
            Workout(
                id = 2,
                name = "Recent Session",
                createdAt = 200L,
                exercises = listOf(
                    WorkoutExercise(
                        exerciseId = 41,
                        exerciseName = "Barbell Squat",
                        sets = listOf(
                            ExerciseSet(setNumber = 1, reps = 5, weightKg = 85f, isCompleted = true),
                            ExerciseSet(setNumber = 2, reps = 8, weightKg = 70f, isCompleted = true, isWarmup = true)
                        )
                    )
                )
            )
        )

        val performance = buildPreviousExercisePerformanceMap(workouts).getValue(41)

        assertEquals("Recent Session", performance.workoutName)
        assertEquals("85 x 5", performance.displayShort)
    }

    @Test
    fun buildProgressionCue_reactsToCurrentSessionProgress() {
        val previous = PreviousExercisePerformance(
            exerciseId = 41,
            exerciseName = "Barbell Squat",
            workoutName = "Recent Session",
            completedAt = 200L,
            topSetWeightKg = 85f,
            topSetReps = 5,
            totalCompletedSets = 3
        )

        val cueBefore = buildProgressionCue(previous, emptyList())
        val cueAfter = buildProgressionCue(
            previous,
            listOf(ExerciseSet(setNumber = 1, reps = 6, weightKg = 85f, isCompleted = true))
        )

        assertTrue(cueBefore.contains("Last session"))
        assertTrue(cueAfter.contains("matched") || cueAfter.contains("ahead"))
    }

    @Test
    fun buildExerciseSubstitutions_prioritizesSameMuscleAlternatives() {
        val currentExercise = WorkoutExercise(
            exerciseId = 1,
            exerciseName = "Barbell Bench Press",
            sets = listOf(ExerciseSet(setNumber = 1))
        )
        val exercises = listOf(
            exercise(id = 1, name = "Barbell Bench Press", muscleGroup = MuscleGroup.CHEST, equipment = Equipment.BARBELL, difficulty = Difficulty.INTERMEDIATE),
            exercise(id = 2, name = "Dumbbell Bench Press", muscleGroup = MuscleGroup.CHEST, equipment = Equipment.DUMBBELL, difficulty = Difficulty.INTERMEDIATE),
            exercise(id = 3, name = "Push-Ups", muscleGroup = MuscleGroup.CHEST, equipment = Equipment.BODYWEIGHT, difficulty = Difficulty.BEGINNER),
            exercise(id = 4, name = "Barbell Row", muscleGroup = MuscleGroup.BACK, equipment = Equipment.BARBELL, difficulty = Difficulty.INTERMEDIATE)
        )

        val substitutions = buildExerciseSubstitutions(currentExercise, exercises, limit = 2)

        assertEquals(listOf("Dumbbell Bench Press", "Push-Ups"), substitutions.map { it.name })
    }

    @Test
    fun applyExerciseSubstitution_preservesSetStructureButClearsCompletedData() {
        val currentExercise = WorkoutExercise(
            exerciseId = 1,
            exerciseName = "Barbell Bench Press",
            sets = listOf(
                ExerciseSet(setNumber = 1, reps = 8, weightKg = 80f, isCompleted = true, isPersonalRecord = true),
                ExerciseSet(setNumber = 2, reps = 8, weightKg = 80f, isCompleted = false)
            )
        )

        val updated = applyExerciseSubstitution(
            workoutExercise = currentExercise,
            substitute = exercise(
                id = 6,
                name = "Dumbbell Bench Press",
                muscleGroup = MuscleGroup.CHEST,
                equipment = Equipment.DUMBBELL,
                difficulty = Difficulty.INTERMEDIATE
            )
        )

        assertEquals("Dumbbell Bench Press", updated.exerciseName)
        assertTrue(updated.sets.orEmpty().all { !it.isCompleted })
        assertTrue(updated.sets.orEmpty().all { !it.isPersonalRecord })
        assertTrue(updated.sets.orEmpty().all { it.weightKg == 0f })
        assertEquals(listOf(8, 8), updated.sets.orEmpty().map { it.reps })
    }

    @Test
    fun canSubstituteExercise_blocksSwapAfterCompletedSet() {
        val startedExercise = WorkoutExercise(
            exerciseId = 1,
            exerciseName = "Barbell Bench Press",
            sets = listOf(ExerciseSet(setNumber = 1, isCompleted = true))
        )

        assertFalse(canSubstituteExercise(startedExercise))
    }

    @Test
    fun buildWorkoutSummaryInsight_prioritizesPersonalRecords() {
        val workout = Workout(
            name = "Lower Body",
            personalRecordCount = 2,
            exercises = listOf(
                WorkoutExercise(
                    exerciseId = 1,
                    exerciseName = "Back Squat",
                    sets = listOf(
                        ExerciseSet(setNumber = 1, reps = 5, weightKg = 100f, isCompleted = true, isPersonalRecord = true),
                        ExerciseSet(setNumber = 2, reps = 5, weightKg = 100f, isCompleted = true, isPersonalRecord = true)
                    )
                )
            )
        )

        val insight = buildWorkoutSummaryInsight(workout)

        assertEquals("Strong session", insight.title)
        assertTrue(insight.message.contains("2 personal records"))
    }

    @Test
    fun buildWorkoutPersonalRecordHighlights_returnsBestPrForEachExercise() {
        val workout = Workout(
            name = "Upper Body",
            exercises = listOf(
                WorkoutExercise(
                    exerciseId = 1,
                    exerciseName = "Bench Press",
                    sets = listOf(
                        ExerciseSet(setNumber = 1, reps = 5, weightKg = 80f, isCompleted = true, isPersonalRecord = true),
                        ExerciseSet(setNumber = 2, reps = 3, weightKg = 85f, isCompleted = true, isPersonalRecord = true)
                    )
                ),
                WorkoutExercise(
                    exerciseId = 2,
                    exerciseName = "Pull-Up",
                    sets = listOf(
                        ExerciseSet(setNumber = 1, reps = 12, isCompleted = true, isPersonalRecord = true)
                    )
                )
            )
        )

        val highlights = buildWorkoutPersonalRecordHighlights(workout)

        assertEquals(
            listOf("Bench Press: 85 kg x 3", "Pull-Up: 12 reps"),
            highlights
        )
    }

    private fun template(
        name: String,
        difficulty: Difficulty
    ) = WorkoutTemplate(
        name = name,
        description = name,
        category = "Test",
        exercises = listOf(TemplateExercise(exerciseId = 1, exerciseName = "Exercise")),
        difficulty = difficulty,
        isPreset = true
    )

    private fun exercise(
        id: Long,
        name: String,
        muscleGroup: MuscleGroup,
        equipment: Equipment,
        difficulty: Difficulty
    ) = Exercise(
        id = id,
        name = name,
        description = name,
        category = ExerciseCategory.STRENGTH,
        muscleGroup = muscleGroup,
        equipment = equipment,
        difficulty = difficulty
    )
}
