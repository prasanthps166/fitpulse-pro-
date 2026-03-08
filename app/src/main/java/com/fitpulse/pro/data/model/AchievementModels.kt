package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: AchievementCategory,
    val iconName: String,
    val requirement: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val xpReward: Int = 100
)

enum class AchievementCategory {
    WORKOUT_COUNT, STREAK, VOLUME, CALORIES, SOCIAL, NUTRITION, PERSONAL_RECORD, SPECIAL
}

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val type: ChallengeType,
    val target: Int,
    val currentProgress: Int = 0,
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long,
    val isCompleted: Boolean = false,
    val isActive: Boolean = true,
    val participants: Int = 1,
    val xpReward: Int = 500
)

enum class ChallengeType {
    STEPS, WORKOUTS, CALORIES_BURNED, VOLUME_LIFTED, ACTIVE_MINUTES, WATER_INTAKE, STREAK
}
