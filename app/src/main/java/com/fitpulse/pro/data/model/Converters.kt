package com.fitpulse.pro.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWorkoutExerciseList(value: List<WorkoutExercise>?): String = gson.toJson(value ?: emptyList<WorkoutExercise>())

    @TypeConverter
    fun toWorkoutExerciseList(value: String?): List<WorkoutExercise> =
        try {
            if (value.isNullOrBlank()) emptyList()
            else gson.fromJson(value, object : TypeToken<List<WorkoutExercise>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromTemplateExerciseList(value: List<TemplateExercise>?): String = gson.toJson(value ?: emptyList<TemplateExercise>())

    @TypeConverter
    fun toTemplateExerciseList(value: String?): List<TemplateExercise> =
        try {
            if (value.isNullOrBlank()) emptyList()
            else gson.fromJson(value, object : TypeToken<List<TemplateExercise>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromMuscleGroupList(value: List<MuscleGroup>?): String = gson.toJson(value ?: emptyList<MuscleGroup>())

    @TypeConverter
    fun toMuscleGroupList(value: String?): List<MuscleGroup> =
        try {
            if (value.isNullOrBlank()) emptyList()
            else gson.fromJson(value, object : TypeToken<List<MuscleGroup>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromStringList(value: List<String>?): String = gson.toJson(value ?: emptyList<String>())

    @TypeConverter
    fun toStringList(value: String?): List<String> =
        try {
            if (value.isNullOrBlank()) emptyList()
            else gson.fromJson(value, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory): String = value.name

    @TypeConverter
    fun toExerciseCategory(value: String?): ExerciseCategory =
        parseEnum(value, ExerciseCategory.STRENGTH)

    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String?): MuscleGroup =
        parseEnum(value, MuscleGroup.FULL_BODY)

    @TypeConverter
    fun fromEquipment(value: Equipment): String = value.name

    @TypeConverter
    fun toEquipment(value: String?): Equipment = parseEnum(value, Equipment.NONE)

    @TypeConverter
    fun fromDifficulty(value: Difficulty): String = value.name

    @TypeConverter
    fun toDifficulty(value: String?): Difficulty =
        parseEnum(value, Difficulty.INTERMEDIATE)

    @TypeConverter
    fun fromGender(value: Gender): String = value.name

    @TypeConverter
    fun toGender(value: String?): Gender = parseEnum(value, Gender.MALE)

    @TypeConverter
    fun fromFitnessGoal(value: FitnessGoal): String = value.name

    @TypeConverter
    fun toFitnessGoal(value: String?): FitnessGoal =
        parseEnum(value, FitnessGoal.STAY_FIT)

    @TypeConverter
    fun fromActivityLevel(value: ActivityLevel): String = value.name

    @TypeConverter
    fun toActivityLevel(value: String?): ActivityLevel =
        parseEnum(value, ActivityLevel.MODERATE)

    @TypeConverter
    fun fromUnitSystem(value: UnitSystem): String = value.name

    @TypeConverter
    fun toUnitSystem(value: String?): UnitSystem =
        parseEnum(value, UnitSystem.METRIC)

    @TypeConverter
    fun fromMealType(value: MealType): String = value.name

    @TypeConverter
    fun toMealType(value: String?): MealType = parseEnum(value, MealType.BREAKFAST)

    @TypeConverter
    fun fromPhotoCategory(value: PhotoCategory): String = value.name

    @TypeConverter
    fun toPhotoCategory(value: String?): PhotoCategory =
        parseEnum(value, PhotoCategory.FRONT)

    @TypeConverter
    fun fromRecordType(value: RecordType): String = value.name

    @TypeConverter
    fun toRecordType(value: String?): RecordType =
        parseEnum(value, RecordType.MAX_WEIGHT)

    @TypeConverter
    fun fromAchievementCategory(value: AchievementCategory): String = value.name

    @TypeConverter
    fun toAchievementCategory(value: String?): AchievementCategory =
        parseEnum(value, AchievementCategory.WORKOUT_COUNT)

    @TypeConverter
    fun fromChallengeType(value: ChallengeType): String = value.name

    @TypeConverter
    fun toChallengeType(value: String?): ChallengeType =
        parseEnum(value, ChallengeType.WORKOUTS)

    @TypeConverter
    fun fromMeditationType(value: MeditationType): String = value.name

    @TypeConverter
    fun toMeditationType(value: String?): MeditationType =
        parseEnum(value, MeditationType.GUIDED)

    @TypeConverter
    fun fromWorkoutMood(value: WorkoutMood?): String? = value?.name

    @TypeConverter
    fun toWorkoutMood(value: String?): WorkoutMood? = try {
        value?.let { WorkoutMood.valueOf(it) }
    } catch (e: Exception) {
        null
    }

    private inline fun <reified T : Enum<T>> parseEnum(value: String?, default: T): T {
        return runCatching {
            value?.let { enumValueOf<T>(it) } ?: default
        }.getOrDefault(default)
    }
}
