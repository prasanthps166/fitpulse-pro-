package com.fitpulse.pro.domain.home

import com.fitpulse.pro.data.model.CoachTip
import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.TipCategory
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.navigation.Screen

class CoachTipGenerator {

    fun generate(todayStats: DailyStats?, profile: UserProfile?): List<CoachTip> {
        val tips = mutableListOf<CoachTip>()
        val dailyWaterGoal = profile?.dailyWaterGoalMl ?: DEFAULT_WATER_GOAL_ML

        if (todayStats == null || todayStats.workoutCount == 0) {
            tips += CoachTip(
                id = "tip_workout",
                message = "You have not worked out today yet. Make time for a session.",
                category = TipCategory.WORKOUT_SUGGESTION,
                priority = 10,
                actionLabel = "Start Workout",
                actionRoute = Screen.Workouts.route
            )
        }

        if (todayStats == null || todayStats.waterMl < dailyWaterGoal / 2) {
            tips += CoachTip(
                id = "tip_water",
                message = "You are behind on hydration. Drink some water soon.",
                category = TipCategory.NUTRITION_TIP,
                priority = 5
            )
        }

        if ((todayStats?.steps ?: 0) < STEP_GOAL_THRESHOLD) {
            tips += CoachTip(
                id = "tip_steps",
                message = "Try to add a short walk today. Even 15 minutes helps.",
                category = TipCategory.WORKOUT_SUGGESTION,
                priority = 3
            )
        }

        tips += CoachTip(
            id = "tip_form",
            message = "Focus on mind-muscle connection during your next workout.",
            category = TipCategory.FORM_TIP,
            priority = 1
        )
        tips += CoachTip(
            id = "tip_recovery",
            message = "Do not skip stretching. Five minutes after training goes a long way.",
            category = TipCategory.RECOVERY,
            priority = 1
        )
        tips += CoachTip(
            id = "tip_motivation",
            message = "Consistency beats perfection. Show up even on hard days.",
            category = TipCategory.MOTIVATION,
            priority = 1
        )

        return tips.sortedByDescending { it.priority }
    }

    private companion object {
        const val DEFAULT_WATER_GOAL_ML = 3000
        const val STEP_GOAL_THRESHOLD = 5000
    }
}
