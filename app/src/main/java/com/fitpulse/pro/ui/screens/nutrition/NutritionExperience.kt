package com.fitpulse.pro.ui.screens.nutrition

import com.fitpulse.pro.data.model.MealEntry
import com.fitpulse.pro.data.model.MealType
import com.fitpulse.pro.data.model.WaterIntake
import com.fitpulse.pro.utils.Utils
import java.util.Calendar

internal enum class NutritionGuidanceTone {
    POSITIVE,
    NEUTRAL,
    CAUTION
}

internal data class DailyNutritionGuidance(
    val title: String,
    val message: String,
    val nextAction: String,
    val tone: NutritionGuidanceTone
)

internal data class MealTemplatePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val meal: MealEntry
)

internal data class WeeklyNutritionDaySummary(
    val label: String,
    val calorieGoalHit: Boolean,
    val proteinGoalHit: Boolean,
    val waterGoalHit: Boolean
)

internal data class WeeklyNutritionSummary(
    val headline: String,
    val message: String,
    val calorieGoalDays: Int,
    val proteinGoalDays: Int,
    val waterGoalDays: Int,
    val days: List<WeeklyNutritionDaySummary>
)

internal fun nutritionMealTemplates(): List<MealTemplatePreset> {
    return listOf(
        MealTemplatePreset(
            id = "protein_oats",
            title = "Protein Oats",
            subtitle = "Fast breakfast with carbs and protein",
            meal = MealEntry(
                name = "Protein Oats",
                mealType = MealType.BREAKFAST,
                calories = 430,
                proteinGrams = 32f,
                carbsGrams = 52f,
                fatGrams = 10f
            )
        ),
        MealTemplatePreset(
            id = "chicken_rice_bowl",
            title = "Chicken Rice Bowl",
            subtitle = "Simple high-protein lunch base",
            meal = MealEntry(
                name = "Chicken Rice Bowl",
                mealType = MealType.LUNCH,
                calories = 560,
                proteinGrams = 42f,
                carbsGrams = 63f,
                fatGrams = 14f
            )
        ),
        MealTemplatePreset(
            id = "lean_dinner_plate",
            title = "Lean Dinner Plate",
            subtitle = "Balanced dinner with easy portions",
            meal = MealEntry(
                name = "Lean Dinner Plate",
                mealType = MealType.DINNER,
                calories = 640,
                proteinGrams = 45f,
                carbsGrams = 55f,
                fatGrams = 18f
            )
        ),
        MealTemplatePreset(
            id = "greek_yogurt_snack",
            title = "Greek Yogurt Bowl",
            subtitle = "Easy snack to bring protein up",
            meal = MealEntry(
                name = "Greek Yogurt Bowl",
                mealType = MealType.SNACK,
                calories = 280,
                proteinGrams = 22f,
                carbsGrams = 30f,
                fatGrams = 6f
            )
        ),
        MealTemplatePreset(
            id = "pre_workout_fuel",
            title = "Pre-Workout Fuel",
            subtitle = "Light carbs before training",
            meal = MealEntry(
                name = "Pre-Workout Fuel",
                mealType = MealType.PRE_WORKOUT,
                calories = 180,
                proteinGrams = 4f,
                carbsGrams = 32f,
                fatGrams = 2f
            )
        ),
        MealTemplatePreset(
            id = "recovery_wrap",
            title = "Recovery Wrap",
            subtitle = "Post-workout meal with protein and carbs",
            meal = MealEntry(
                name = "Recovery Wrap",
                mealType = MealType.POST_WORKOUT,
                calories = 420,
                proteinGrams = 35f,
                carbsGrams = 40f,
                fatGrams = 12f
            )
        )
    )
}

internal fun buildDailyNutritionGuidance(
    calories: Int,
    calorieGoal: Int,
    proteinGrams: Float,
    proteinGoal: Int,
    waterMl: Int,
    waterGoalMl: Int,
    mealsLogged: Int
): DailyNutritionGuidance {
    if (mealsLogged == 0 && waterMl == 0) {
        return DailyNutritionGuidance(
            title = "Start the day with one clear log",
            message = "The easiest way to stay on track is to log your first real meal and your first glass of water early.",
            nextAction = "Log breakfast or lunch, then add a water entry.",
            tone = NutritionGuidanceTone.CAUTION
        )
    }

    val caloriesOnTrack = calorieGoal > 0 && calories in (calorieGoal * 0.8f).toInt()..(calorieGoal * 1.1f).toInt()
    val proteinOnTrack = proteinGoal > 0 && proteinGrams >= proteinGoal * 0.8f
    val waterOnTrack = waterGoalMl > 0 && waterMl >= waterGoalMl * 0.75f
    val score = listOf(caloriesOnTrack, proteinOnTrack, waterOnTrack).count { it }

    return when {
        score == 3 && mealsLogged >= 3 -> DailyNutritionGuidance(
            title = "Nutrition is under control today",
            message = "Calories, protein, and hydration are all in a productive range. Keep dinner simple and do not over-correct.",
            nextAction = "Repeat what is already working.",
            tone = NutritionGuidanceTone.POSITIVE
        )
        proteinGoal > 0 && proteinGrams < proteinGoal * 0.6f -> DailyNutritionGuidance(
            title = "Protein is the main gap right now",
            message = "Calories alone do not protect recovery. Bring protein up before spending the rest of the day on snacks or extras.",
            nextAction = "Add one protein-focused meal or snack next.",
            tone = NutritionGuidanceTone.CAUTION
        )
        waterGoalMl > 0 && waterMl < waterGoalMl * 0.5f -> DailyNutritionGuidance(
            title = "Hydration is lagging behind",
            message = "Your food intake may be fine, but hydration is still low enough to drag energy and training quality down.",
            nextAction = "Add 250-500ml now and keep sipping through the next meal.",
            tone = NutritionGuidanceTone.CAUTION
        )
        calorieGoal > 0 && calories > calorieGoal * 1.15f -> DailyNutritionGuidance(
            title = "Calories are already running high",
            message = "The day is not ruined, but the easiest fix is to keep the remaining meals boring and protein-forward.",
            nextAction = "Use a lighter meal template and skip low-satiety extras.",
            tone = NutritionGuidanceTone.NEUTRAL
        )
        mealsLogged < 3 -> DailyNutritionGuidance(
            title = "The day is still recoverable",
            message = "You have some structure, but meal consistency is still loose. A stable plan usually looks like 3 anchor meals before extras.",
            nextAction = "Build the next meal around protein and a clear portion of carbs.",
            tone = NutritionGuidanceTone.NEUTRAL
        )
        else -> DailyNutritionGuidance(
            title = "You are close to a solid day",
            message = "The base is there. Tighten the weakest metric instead of trying to optimize everything at once.",
            nextAction = "Finish the day by closing the smallest gap.",
            tone = NutritionGuidanceTone.NEUTRAL
        )
    }
}

internal fun buildWeeklyNutritionSummary(
    meals: List<MealEntry>,
    waterEntries: List<WaterIntake>,
    calorieGoal: Int,
    proteinGoal: Int,
    waterGoalMl: Int,
    referenceTimeMillis: Long = System.currentTimeMillis()
): WeeklyNutritionSummary {
    val todayStart = startOfDay(referenceTimeMillis)
    val days = (6 downTo 0).map { daysBack ->
        val dayStart = shiftDays(todayStart, -daysBack)
        val dayEnd = endOfDay(dayStart)
        val dayMeals = meals.filter { it.date in dayStart..dayEnd }
        val dayWater = waterEntries.filter { it.date in dayStart..dayEnd }
        val totalCalories = dayMeals.sumOf { it.calories }
        val totalProtein = dayMeals.sumOf { it.proteinGrams.toDouble() }.toFloat()
        val totalWater = dayWater.sumOf { it.amountMl }

        WeeklyNutritionDaySummary(
            label = Utils.formatDayOfWeek(dayStart),
            calorieGoalHit = calorieGoal > 0 && totalCalories in (calorieGoal * 0.8f).toInt()..(calorieGoal * 1.1f).toInt(),
            proteinGoalHit = proteinGoal > 0 && totalProtein >= proteinGoal * 0.8f,
            waterGoalHit = waterGoalMl > 0 && totalWater >= waterGoalMl * 0.75f
        )
    }

    val calorieGoalDays = days.count { it.calorieGoalHit }
    val proteinGoalDays = days.count { it.proteinGoalHit }
    val waterGoalDays = days.count { it.waterGoalHit }

    val headline = when {
        calorieGoalDays + proteinGoalDays + waterGoalDays >= 15 -> "Weekly nutrition is holding up"
        proteinGoalDays <= 2 -> "Protein consistency needs the most work"
        waterGoalDays <= 2 -> "Hydration is the easiest win for next week"
        calorieGoalDays <= 2 -> "Calories need tighter guardrails this week"
        else -> "Consistency is building"
    }

    val message = "$calorieGoalDays of 7 days hit calorie range, $proteinGoalDays hit protein, and $waterGoalDays hit hydration."

    return WeeklyNutritionSummary(
        headline = headline,
        message = message,
        calorieGoalDays = calorieGoalDays,
        proteinGoalDays = proteinGoalDays,
        waterGoalDays = waterGoalDays,
        days = days
    )
}

private fun startOfDay(timestamp: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun endOfDay(timestamp: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}

private fun shiftDays(timestamp: Long, days: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = timestamp
        add(Calendar.DAY_OF_YEAR, days)
    }.timeInMillis
}
