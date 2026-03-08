package com.fitpulse.pro.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val email: String = "",
    val avatarUri: String? = null,
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val fitnessGoal: FitnessGoal = FitnessGoal.STAY_FIT,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val dailyCalorieGoal: Int = 2000,
    val dailyProteinGoal: Int = 150,
    val dailyCarbsGoal: Int = 250,
    val dailyFatGoal: Int = 65,
    val dailyWaterGoalMl: Int = 3000,
    val dailyStepsGoal: Int = 10000,
    val unitSystem: UnitSystem = UnitSystem.METRIC,
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class Gender { MALE, FEMALE, OTHER }
enum class FitnessGoal { LOSE_WEIGHT, BUILD_MUSCLE, STAY_FIT, IMPROVE_ENDURANCE, INCREASE_FLEXIBILITY }
enum class ActivityLevel { SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE }
enum class UnitSystem { METRIC, IMPERIAL }
