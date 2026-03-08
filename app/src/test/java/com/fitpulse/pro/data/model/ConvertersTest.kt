package com.fitpulse.pro.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun enumConverters_fallBackToDefaultsForUnexpectedValues() {
        assertEquals(ExerciseCategory.STRENGTH, converters.toExerciseCategory("OLD_STRENGTH"))
        assertEquals(MuscleGroup.FULL_BODY, converters.toMuscleGroup("TORSO"))
        assertEquals(Equipment.NONE, converters.toEquipment("PLATE_MACHINE"))
        assertEquals(Difficulty.INTERMEDIATE, converters.toDifficulty("PRO"))
        assertEquals(Gender.MALE, converters.toGender("UNKNOWN"))
        assertEquals(FitnessGoal.STAY_FIT, converters.toFitnessGoal("MAINTAIN"))
        assertEquals(ActivityLevel.MODERATE, converters.toActivityLevel("EXTREME"))
        assertEquals(UnitSystem.METRIC, converters.toUnitSystem("CUSTOM"))
        assertEquals(MealType.BREAKFAST, converters.toMealType("BRUNCH"))
        assertEquals(PhotoCategory.FRONT, converters.toPhotoCategory("TRANSFORMATION"))
        assertEquals(RecordType.MAX_WEIGHT, converters.toRecordType("REP_MAX"))
        assertEquals(
            AchievementCategory.WORKOUT_COUNT,
            converters.toAchievementCategory("CONSISTENCY")
        )
        assertEquals(ChallengeType.WORKOUTS, converters.toChallengeType("MILES"))
        assertEquals(MeditationType.GUIDED, converters.toMeditationType("ZEN"))
    }

    @Test
    fun enumConverters_fallBackToDefaultsForNullValues() {
        assertEquals(ExerciseCategory.STRENGTH, converters.toExerciseCategory(null))
        assertEquals(ActivityLevel.MODERATE, converters.toActivityLevel(null))
        assertEquals(MealType.BREAKFAST, converters.toMealType(null))
        assertEquals(MeditationType.GUIDED, converters.toMeditationType(null))
    }

    @Test
    fun workoutMoodConverter_returnsNullForUnexpectedValues() {
        assertNull(converters.toWorkoutMood("OVERTRAINED"))
        assertNull(converters.toWorkoutMood(null))
    }
}
