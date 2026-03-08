package com.fitpulse.pro.domain.achievements

import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.repository.FitPulseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AchievementManager(
    private val repository: FitPulseRepository,
    private val achievementProgressEvaluator: AchievementProgressEvaluator,
    private val currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
) {

    val achievements: Flow<List<Achievement>> = repository.getAllAchievements()
    val unlockedAchievementCount: Flow<Int> = repository.getUnlockedCount()

    suspend fun syncProgress(): List<Achievement> {
        val lockedAchievements = repository.getAllAchievements().first().filterNot { it.isUnlocked }
        if (lockedAchievements.isEmpty()) {
            return emptyList()
        }

        val snapshot = AchievementProgressSnapshot(
            workoutCount = repository.getTotalWorkoutCount().first(),
            totalVolume = repository.getTotalVolume().first() ?: 0f,
            maxStreak = repository.getMaxStreak().first() ?: 0,
            personalRecordCount = repository.getAllRecords().first().size,
            meditationSessionCount = repository.getAllMeditationSessions().first().size
        )
        val unlockedAtMillis = currentTimeMillisProvider()
        val unlockedAchievements = mutableListOf<Achievement>()

        for (achievement in lockedAchievements) {
            val update = achievementProgressEvaluator.evaluate(
                achievement = achievement,
                snapshot = snapshot,
                unlockedAtMillis = unlockedAtMillis
            ) ?: continue

            repository.updateAchievement(update.achievement)
            if (update.wasUnlocked) {
                unlockedAchievements += update.achievement
            }
        }

        return unlockedAchievements
    }

    suspend fun unlockSpecialAchievement(achievementId: String): Achievement? {
        val achievement = repository.getAllAchievements().first().find { it.id == achievementId }
            ?: return null
        val update = achievementProgressEvaluator.unlockSpecial(
            achievement = achievement,
            unlockedAtMillis = currentTimeMillisProvider()
        ) ?: return null

        repository.updateAchievement(update.achievement)
        return update.achievement
    }
}
