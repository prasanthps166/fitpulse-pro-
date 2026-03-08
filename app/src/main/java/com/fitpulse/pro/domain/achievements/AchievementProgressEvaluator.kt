package com.fitpulse.pro.domain.achievements

import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.AchievementCategory

data class AchievementProgressSnapshot(
    val workoutCount: Int,
    val totalVolume: Float,
    val maxStreak: Int,
    val personalRecordCount: Int,
    val meditationSessionCount: Int
)

data class AchievementUpdateResult(
    val achievement: Achievement,
    val wasUnlocked: Boolean
)

class AchievementProgressEvaluator {

    fun evaluate(
        achievement: Achievement,
        snapshot: AchievementProgressSnapshot,
        unlockedAtMillis: Long
    ): AchievementUpdateResult? {
        val currentProgress = resolveProgress(achievement, snapshot)
        val shouldUnlock = shouldUnlock(achievement, currentProgress)

        return when {
            shouldUnlock -> AchievementUpdateResult(
                achievement = achievement.copy(
                    isUnlocked = true,
                    currentProgress = achievement.requirement,
                    unlockedAt = unlockedAtMillis
                ),
                wasUnlocked = true
            )

            currentProgress != achievement.currentProgress -> AchievementUpdateResult(
                achievement = achievement.copy(currentProgress = currentProgress),
                wasUnlocked = false
            )

            else -> null
        }
    }

    fun unlockSpecial(achievement: Achievement, unlockedAtMillis: Long): AchievementUpdateResult? {
        if (achievement.isUnlocked) {
            return null
        }
        return AchievementUpdateResult(
            achievement = achievement.copy(
                isUnlocked = true,
                currentProgress = achievement.requirement,
                unlockedAt = unlockedAtMillis
            ),
            wasUnlocked = true
        )
    }

    private fun resolveProgress(
        achievement: Achievement,
        snapshot: AchievementProgressSnapshot
    ): Int = when (achievement.category) {
        AchievementCategory.WORKOUT_COUNT -> snapshot.workoutCount
        AchievementCategory.STREAK -> snapshot.maxStreak
        AchievementCategory.VOLUME -> snapshot.totalVolume.toInt()
        AchievementCategory.PERSONAL_RECORD -> snapshot.personalRecordCount
        AchievementCategory.SPECIAL -> {
            if (achievement.id.contains("meditation")) {
                snapshot.meditationSessionCount
            } else {
                achievement.currentProgress
            }
        }

        AchievementCategory.NUTRITION,
        AchievementCategory.SOCIAL,
        AchievementCategory.CALORIES -> achievement.currentProgress
    }

    private fun shouldUnlock(achievement: Achievement, currentProgress: Int): Boolean =
        when (achievement.category) {
            AchievementCategory.NUTRITION,
            AchievementCategory.SOCIAL,
            AchievementCategory.CALORIES -> false

            AchievementCategory.SPECIAL -> {
                achievement.id.contains("meditation") &&
                    currentProgress >= achievement.requirement
            }

            else -> currentProgress >= achievement.requirement
        }
}
