package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_templates")
data class WorkoutTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "",
    val exercises: List<TemplateExercise> = emptyList(),
    val estimatedDurationMinutes: Int = 45,
    val difficulty: Difficulty = Difficulty.INTERMEDIATE,
    val isPreset: Boolean = false,
    val timesUsed: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class TemplateExercise(
    val exerciseId: Long,
    val exerciseName: String,
    val targetSets: Int = 3,
    val targetReps: Int = 10,
    val targetWeightKg: Float = 0f,
    val restSeconds: Int = 60,
    val orderIndex: Int = 0
)
