package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: ExerciseCategory,
    val muscleGroup: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: Equipment = Equipment.NONE,
    val difficulty: Difficulty = Difficulty.INTERMEDIATE,
    val instructions: List<String> = emptyList(),
    val tips: List<String> = emptyList(),
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val isCustom: Boolean = false,
    val caloriesPerMinute: Float = 5f
)

enum class ExerciseCategory {
    STRENGTH, CARDIO, FLEXIBILITY, BALANCE, PLYOMETRICS, CALISTHENICS, OLYMPIC_LIFTING, YOGA
}

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, FOREARMS,
    QUADRICEPS, HAMSTRINGS, GLUTES, CALVES, ABS, OBLIQUES,
    TRAPS, LATS, LOWER_BACK, HIP_FLEXORS, FULL_BODY, CARDIO_SYSTEM
}

enum class Equipment {
    NONE, BARBELL, DUMBBELL, CABLE, MACHINE, KETTLEBELL,
    RESISTANCE_BAND, BODYWEIGHT, PULL_UP_BAR, BENCH, SMITH_MACHINE,
    TREADMILL, BIKE, ELLIPTICAL, ROWING_MACHINE, JUMP_ROPE
}

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val notes: String? = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val durationMinutes: Int = 0,
    val totalCalories: Int = 0,
    val totalVolume: Float = 0f,
    val exercises: List<WorkoutExercise>? = emptyList(),
    val isTemplate: Boolean = false,
    val templateName: String? = null,
    val mood: WorkoutMood? = null,
    val rating: Int = 0,
    val personalRecordCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class WorkoutExercise(
    val exerciseId: Long,
    val exerciseName: String,
    val sets: List<ExerciseSet>? = emptyList(),
    val notes: String? = "",
    val restSeconds: Int = 60,
    val orderIndex: Int = 0,
    val supersetGroupId: Int? = null
)

data class ExerciseSet(
    val setNumber: Int,
    val reps: Int = 0,
    val weightKg: Float = 0f,
    val durationSeconds: Int = 0,
    val distanceMeters: Float = 0f,
    val isWarmup: Boolean = false,
    val isDropSet: Boolean = false,
    val isFailure: Boolean = false,
    val isCompleted: Boolean = false,
    val rpe: Float? = null,
    val isPersonalRecord: Boolean = false
) {
    fun estimatedOneRepMax(): Float {
        if (weightKg <= 0f || reps <= 0) return 0f
        return if (reps == 1) weightKg else weightKg * (1f + reps / 30f)
    }
}

enum class WorkoutMood { GREAT, GOOD, OKAY, TIRED, TERRIBLE }
