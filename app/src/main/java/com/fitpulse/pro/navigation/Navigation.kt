package com.fitpulse.pro.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.School
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector = Icons.Default.Circle,
    val icon: ImageVector = Icons.Default.Circle
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Workouts : Screen(
        "workouts",
        "Workouts",
        Icons.Filled.FitnessCenter,
        Icons.Outlined.FitnessCenter
    )
    object Nutrition : Screen(
        "nutrition",
        "Nutrition",
        Icons.Filled.Restaurant,
        Icons.Outlined.Restaurant
    )
    object Progress : Screen(
        "progress",
        "Progress",
        Icons.AutoMirrored.Filled.TrendingUp,
        Icons.AutoMirrored.Outlined.TrendingUp
    )
    object Learn : Screen("learn", "Knowledge", Icons.Filled.School, Icons.Outlined.School)
    object Onboarding : Screen("onboarding", "Get Started")
    object Profile : Screen("profile", "Profile")
    object ActiveWorkout : Screen("active_workout", "Active Workout")
    object ExercisePicker : Screen("exercise_picker", "Exercise Picker")

    object WorkoutDetail : Screen("workout_detail/{workoutId}", "Workout Detail") {
        fun createRoute(workoutId: Long) = "workout_detail/$workoutId"
    }

    object ExerciseDetail : Screen("exercise_detail/{exerciseId}", "Exercise Detail") {
        fun createRoute(exerciseId: Long) = "exercise_detail/$exerciseId"
    }

    object ExerciseLibrary : Screen(
        "exercise_library?muscleGroup={muscleGroup}",
        "Exercise Library"
    ) {
        fun createRoute(muscleGroup: String? = null) =
            if (muscleGroup != null) {
                "exercise_library?muscleGroup=$muscleGroup"
            } else {
                "exercise_library"
            }
    }

    object ArticleDetail : Screen("article/{articleId}", "Article") {
        fun createRoute(articleId: String) = "article/$articleId"
    }

    object MealLog : Screen("meal_log", "Log Meal")
    object Achievements : Screen("achievements", "Achievements")
    object Challenges : Screen("challenges", "Challenges")
    object Settings : Screen("settings", "Settings")
    object MeditationTimer : Screen("meditation_timer", "Meditation")
    object BreathingExercise : Screen("breathing_exercise", "Breathing")
    object WorkoutSummary : Screen("workout_summary", "Workout Summary")
    object BodyMeasurementLog : Screen("body_measurement_log", "Log Measurement")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Workouts,
    Screen.Nutrition,
    Screen.Progress,
    Screen.Learn
)
