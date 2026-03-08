package com.fitpulse.pro.ui

object TestTags {
    const val OnboardingScreen = "onboarding_screen"
    const val OnboardingSkipButton = "onboarding_skip_button"
    const val OnboardingPrimaryButton = "onboarding_primary_button"

    const val HomeScreen = "home_screen"
    const val HomeFocusCard = "home_focus_card"
    const val HomeSummarySection = "home_summary_section"
    const val HomeWaterCard = "home_water_card"
    const val HomeQuickStartSection = "home_quick_start_section"
    const val HomeRecentWorkoutsEmptyState = "home_recent_workouts_empty_state"

    const val WorkoutScreen = "workout_screen"
    const val NutritionScreen = "nutrition_screen"
    const val ProgressScreen = "progress_screen"
    const val LearnScreen = "learn_screen"
    const val SocialScreen = "social_screen"

    fun bottomNavItem(route: String): String = "bottom_nav_$route"
}
