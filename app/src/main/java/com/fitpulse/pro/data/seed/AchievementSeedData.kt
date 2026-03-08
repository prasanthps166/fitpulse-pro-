package com.fitpulse.pro.data.seed

import com.fitpulse.pro.data.model.Achievement
import com.fitpulse.pro.data.model.AchievementCategory

internal fun seedAchievements(): List<Achievement> = listOf(

        Achievement("first_workout", "First Step", "Complete your first workout", AchievementCategory.WORKOUT_COUNT, "fitness_center", 1, xpReward = 50),
        Achievement("10_workouts", "Dedicated", "Complete 10 workouts", AchievementCategory.WORKOUT_COUNT, "fitness_center", 10, xpReward = 100),
        Achievement("25_workouts", "Committed", "Complete 25 workouts", AchievementCategory.WORKOUT_COUNT, "fitness_center", 25, xpReward = 250),
        Achievement("50_workouts", "Iron Will", "Complete 50 workouts", AchievementCategory.WORKOUT_COUNT, "fitness_center", 50, xpReward = 500),
        Achievement("100_workouts", "Century Club", "Complete 100 workouts", AchievementCategory.WORKOUT_COUNT, "fitness_center", 100, xpReward = 1000),
        Achievement("7_day_streak", "Week Warrior", "Maintain a 7-day streak", AchievementCategory.STREAK, "local_fire_department", 7, xpReward = 200),
        Achievement("30_day_streak", "Monthly Master", "Maintain a 30-day streak", AchievementCategory.STREAK, "local_fire_department", 30, xpReward = 500),
        Achievement("90_day_streak", "Quarter Champion", "Maintain a 90-day streak", AchievementCategory.STREAK, "local_fire_department", 90, xpReward = 1500),
        Achievement("365_day_streak", "Year of Iron", "Maintain a 365-day streak", AchievementCategory.STREAK, "local_fire_department", 365, xpReward = 5000),
        Achievement("1000kg_volume", "Ton Lifter", "Lift a total of 1,000 kg", AchievementCategory.VOLUME, "monitor_weight", 1000, xpReward = 200),
        Achievement("10000kg_volume", "10 Ton Club", "Lift a total of 10,000 kg", AchievementCategory.VOLUME, "monitor_weight", 10000, xpReward = 500),
        Achievement("100000kg_volume", "Iron Giant", "Lift a total of 100,000 kg", AchievementCategory.VOLUME, "monitor_weight", 100000, xpReward = 2000),
        Achievement("first_pr", "Record Breaker", "Set your first personal record", AchievementCategory.PERSONAL_RECORD, "emoji_events", 1, xpReward = 100),
        Achievement("10_prs", "PR Machine", "Set 10 personal records", AchievementCategory.PERSONAL_RECORD, "emoji_events", 10, xpReward = 500),
        Achievement("log_meals_7", "Meal Tracker", "Log meals for 7 days", AchievementCategory.NUTRITION, "restaurant", 7, xpReward = 150),
        Achievement("log_meals_30", "Nutrition Master", "Log meals for 30 days", AchievementCategory.NUTRITION, "restaurant", 30, xpReward = 400),
        Achievement("water_goal_7", "Hydration Hero", "Hit water goal 7 days in a row", AchievementCategory.NUTRITION, "water_drop", 7, xpReward = 150),
        Achievement("first_challenge", "Challenger", "Complete your first challenge", AchievementCategory.SOCIAL, "flag", 1, xpReward = 200),
        Achievement("5_challenges", "Challenge Master", "Complete 5 challenges", AchievementCategory.SOCIAL, "flag", 5, xpReward = 500),
        Achievement("early_bird", "Early Bird", "Complete a workout before 7 AM", AchievementCategory.SPECIAL, "wb_sunny", 1, xpReward = 150),
        Achievement("night_owl", "Night Owl", "Complete a workout after 10 PM", AchievementCategory.SPECIAL, "nightlight", 1, xpReward = 150),
        Achievement("meditation_starter", "Mindful", "Complete 5 meditation sessions", AchievementCategory.SPECIAL, "self_improvement", 5, xpReward = 200)
)
