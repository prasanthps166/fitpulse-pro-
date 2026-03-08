package com.fitpulse.pro.ui.screens.workout

import com.fitpulse.pro.data.model.Difficulty
import com.fitpulse.pro.data.model.Exercise
import com.fitpulse.pro.data.model.ExerciseSet
import com.fitpulse.pro.data.model.MuscleGroup
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutTemplate
import kotlin.math.abs

internal data class PreviousExercisePerformance(
    val exerciseId: Long,
    val exerciseName: String,
    val workoutName: String,
    val completedAt: Long,
    val topSetWeightKg: Float,
    val topSetReps: Int,
    val totalCompletedSets: Int
) {
    val displayShort: String
        get() = when {
            topSetWeightKg > 0f && topSetReps > 0 -> "${trimNumber(topSetWeightKg)} x $topSetReps"
            topSetReps > 0 -> "$topSetReps reps"
            else -> "-"
        }

    fun estimatedOneRepMax(): Float {
        if (topSetWeightKg <= 0f || topSetReps <= 0) return 0f
        return if (topSetReps == 1) topSetWeightKg else topSetWeightKg * (1f + topSetReps / 30f)
    }
}

internal data class WorkoutSummaryInsight(
    val title: String,
    val message: String
)

internal fun buildQuickStartTemplates(templates: List<WorkoutTemplate>): List<WorkoutTemplate> {
    val preferredNames = listOf(
        "Full Body Starter",
        "Gym Foundation A",
        "Dumbbell Full Body",
        "General Fitness Express",
        "StrongLifts 5x5 A",
        "Full Body Progression"
    )
    val templateByName = templates.associateBy { it.name }
    val preferred = preferredNames.mapNotNull(templateByName::get)
    val remaining = templates
        .sortedWith(
            compareBy<WorkoutTemplate> { it.difficulty.ordinal }
                .thenBy { it.estimatedDurationMinutes }
                .thenBy { it.name }
        )
        .filterNot { template -> preferred.any { it.name == template.name } }

    return preferred + remaining
}

internal fun buildExerciseSubstitutions(
    workoutExercise: WorkoutExercise,
    allExercises: List<Exercise>,
    limit: Int = 3
): List<Exercise> {
    if (limit <= 0) return emptyList()

    val sourceExercise = allExercises.firstOrNull { it.id == workoutExercise.exerciseId }
        ?: allExercises.firstOrNull { it.name.equals(workoutExercise.exerciseName, ignoreCase = true) }
        ?: return emptyList()

    return allExercises
        .asSequence()
        .filter { candidate -> candidate.id != sourceExercise.id }
        .sortedWith(
            compareByDescending<Exercise> { candidate ->
                substitutionScore(sourceExercise, candidate)
            }.thenBy { candidate ->
                difficultyDistance(sourceExercise.difficulty, candidate.difficulty)
            }.thenBy { candidate ->
                candidate.name
            }
        )
        .take(limit)
        .toList()
}

internal fun canSubstituteExercise(workoutExercise: WorkoutExercise): Boolean =
    workoutExercise.sets.orEmpty().none { it.isCompleted }

internal fun applyExerciseSubstitution(
    workoutExercise: WorkoutExercise,
    substitute: Exercise
): WorkoutExercise {
    val updatedSets = workoutExercise.sets.orEmpty()
        .ifEmpty { listOf(ExerciseSet(setNumber = 1)) }
        .map { set ->
            set.copy(
                weightKg = 0f,
                durationSeconds = 0,
                distanceMeters = 0f,
                isCompleted = false,
                isPersonalRecord = false
            )
        }

    return workoutExercise.copy(
        exerciseId = substitute.id,
        exerciseName = substitute.name,
        sets = updatedSets
    )
}

internal fun buildWorkoutSummaryInsight(workout: Workout): WorkoutSummaryInsight {
    val exercises = workout.exercises.orEmpty()
    val totalSets = exercises.sumOf { it.sets.orEmpty().size }
    val completedSets = exercises.sumOf { exercise -> exercise.sets.orEmpty().count { it.isCompleted } }
    val completionRatio = if (totalSets == 0) 0f else completedSets.toFloat() / totalSets.toFloat()

    return when {
        workout.personalRecordCount > 0 -> WorkoutSummaryInsight(
            title = "Strong session",
            message = "You logged ${workout.personalRecordCount} personal record${if (workout.personalRecordCount == 1) "" else "s"} and completed $completedSets of $totalSets sets."
        )
        completionRatio >= 0.9f -> WorkoutSummaryInsight(
            title = "Solid work",
            message = "You completed nearly everything planned and kept the session moving."
        )
        completionRatio >= 0.6f -> WorkoutSummaryInsight(
            title = "Useful training day",
            message = "You banked meaningful work today. Keep the next session simple and build from here."
        )
        completedSets > 0 -> WorkoutSummaryInsight(
            title = "Session logged",
            message = "Not every workout needs to be perfect. The important part is that the work is recorded and repeatable."
        )
        else -> WorkoutSummaryInsight(
            title = "Workout saved",
            message = "This session is logged. Tighten the plan and come back with a clearer target next time."
        )
    }
}

internal fun buildWorkoutPersonalRecordHighlights(workout: Workout): List<String> {
    return workout.exercises.orEmpty().mapNotNull { exercise ->
        val bestPrSet = exercise.sets.orEmpty()
            .filter { it.isPersonalRecord }
            .maxWithOrNull(
                compareBy<ExerciseSet> { it.estimatedOneRepMax() }
                    .thenBy { it.reps }
                    .thenBy { it.weightKg }
            )

        bestPrSet?.let { set ->
            val loadText = if (set.weightKg > 0f) "${trimNumber(set.weightKg)} kg x ${set.reps}" else "${set.reps} reps"
            "${exercise.exerciseName}: $loadText"
        }
    }
}

internal fun buildPreviousExercisePerformanceMap(
    recentWorkouts: List<Workout>
): Map<Long, PreviousExercisePerformance> {
    val performanceByExercise = linkedMapOf<Long, PreviousExercisePerformance>()

    recentWorkouts
        .sortedByDescending { it.endTime ?: it.createdAt }
        .forEach { workout ->
            workout.exercises.orEmpty().forEach exerciseLoop@{ exercise ->
                if (exercise.exerciseId in performanceByExercise) {
                    return@exerciseLoop
                }

                val bestSet = exercise.sets
                    .orEmpty()
                    .filter { it.isCompleted && !it.isWarmup && (it.weightKg > 0f || it.reps > 0) }
                    .maxWithOrNull(
                        compareBy<ExerciseSet> { it.estimatedOneRepMax() }
                            .thenBy { it.reps }
                            .thenBy { it.weightKg }
                    )

                if (bestSet != null) {
                    performanceByExercise[exercise.exerciseId] = PreviousExercisePerformance(
                        exerciseId = exercise.exerciseId,
                        exerciseName = exercise.exerciseName,
                        workoutName = workout.name,
                        completedAt = workout.endTime ?: workout.createdAt,
                        topSetWeightKg = bestSet.weightKg,
                        topSetReps = bestSet.reps,
                        totalCompletedSets = exercise.sets.orEmpty().count { it.isCompleted && !it.isWarmup }
                    )
                }
            }
        }

    return performanceByExercise
}

internal fun buildProgressionCue(
    previousPerformance: PreviousExercisePerformance?,
    currentSets: List<ExerciseSet>
): String {
    if (previousPerformance == null) {
        return "Start with a smooth working set today, then add load or reps only if form stays clean."
    }

    val currentWorkingSets = currentSets.filter { it.isCompleted && !it.isWarmup }
    if (currentWorkingSets.isEmpty()) {
        return "Last session: ${previousPerformance.displayShort}. Match that first, then add 1 rep or 2.5 kg if bar speed stays solid."
    }

    val bestCurrentSet = currentWorkingSets.maxWithOrNull(
        compareBy<ExerciseSet> { it.estimatedOneRepMax() }
            .thenBy { it.reps }
            .thenBy { it.weightKg }
    )

    if (bestCurrentSet == null) {
        return "Build toward ${previousPerformance.displayShort} with crisp technique today."
    }

    return when {
        bestCurrentSet.estimatedOneRepMax() > previousPerformance.estimatedOneRepMax() + 0.5f -> {
            "You are already ahead of your last logged best for this exercise. Keep the next sets clean instead of rushing more load."
        }

        bestCurrentSet.weightKg == previousPerformance.topSetWeightKg &&
            bestCurrentSet.reps >= previousPerformance.topSetReps -> {
            "You matched your last logged weight. If the next set feels solid, push for 1 more rep before adding load."
        }

        bestCurrentSet.weightKg >= previousPerformance.topSetWeightKg -> {
            "Load is back to your last benchmark. Focus on cleaner reps and controlled range before making bigger jumps."
        }

        else -> {
            "You are still building back toward ${previousPerformance.displayShort}. Use today's sets to regain rhythm, then progress next session."
        }
    }
}

private fun trimNumber(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        String.format("%.1f", value)
    }
}

private fun substitutionScore(source: Exercise, candidate: Exercise): Int {
    val sharedSecondaryMuscles = source.secondaryMuscles.intersect(candidate.secondaryMuscles.toSet()).size
    val sharedPrimaryOrSecondary = (source.secondaryMuscles + source.muscleGroup)
        .toSet()
        .intersect((candidate.secondaryMuscles + candidate.muscleGroup).toSet())
        .size
    val samePrimaryMuscle = if (candidate.muscleGroup == source.muscleGroup) 1 else 0
    val sameCategory = if (candidate.category == source.category) 1 else 0
    val similarDifficulty = 2 - difficultyDistance(source.difficulty, candidate.difficulty).coerceAtMost(2)
    val alternateEquipment = if (candidate.equipment != source.equipment) 1 else 0
    val sharedRegion = if (shareLowerOrUpperFocus(source.muscleGroup, candidate.muscleGroup)) 1 else 0

    return (samePrimaryMuscle * 100) +
        (sharedPrimaryOrSecondary * 25) +
        (sharedSecondaryMuscles * 15) +
        (sameCategory * 12) +
        (similarDifficulty * 10) +
        (alternateEquipment * 4) +
        (sharedRegion * 6)
}

private fun difficultyDistance(source: Difficulty, candidate: Difficulty): Int =
    abs(source.ordinal - candidate.ordinal)

private fun shareLowerOrUpperFocus(
    source: MuscleGroup,
    candidate: MuscleGroup
): Boolean {
    val lowerBodyGroups = setOf(
        MuscleGroup.QUADRICEPS,
        MuscleGroup.HAMSTRINGS,
        MuscleGroup.GLUTES,
        MuscleGroup.CALVES,
        MuscleGroup.HIP_FLEXORS
    )
    val upperBodyGroups = setOf(
        MuscleGroup.CHEST,
        MuscleGroup.BACK,
        MuscleGroup.SHOULDERS,
        MuscleGroup.BICEPS,
        MuscleGroup.TRICEPS,
        MuscleGroup.FOREARMS,
        MuscleGroup.TRAPS,
        MuscleGroup.LATS
    )

    return (source in lowerBodyGroups && candidate in lowerBodyGroups) ||
        (source in upperBodyGroups && candidate in upperBodyGroups)
}
