package com.fitpulse.pro.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitpulse.pro.data.model.UserProfile
import com.fitpulse.pro.ui.screens.home.HomeScreen
import com.fitpulse.pro.ui.screens.learn.ArticleDetailScreen
import com.fitpulse.pro.ui.screens.learn.ExerciseDetailScreen
import com.fitpulse.pro.ui.screens.learn.ExerciseLibraryScreen
import com.fitpulse.pro.ui.screens.learn.LearnScreen
import com.fitpulse.pro.ui.screens.mindfulness.BreathingScreen
import com.fitpulse.pro.ui.screens.mindfulness.MeditationScreen
import com.fitpulse.pro.ui.screens.nutrition.NutritionScreen
import com.fitpulse.pro.ui.screens.onboarding.OnboardingScreen
import com.fitpulse.pro.ui.screens.profile.AchievementsScreen
import com.fitpulse.pro.ui.screens.profile.ProfileScreen
import com.fitpulse.pro.ui.screens.profile.SettingsScreen
import com.fitpulse.pro.ui.screens.progress.MeasurementLogRoute
import com.fitpulse.pro.ui.screens.progress.ProgressScreen
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.screens.workout.ActiveWorkoutScreen
import com.fitpulse.pro.ui.screens.workout.WorkoutDetailScreen
import com.fitpulse.pro.ui.screens.workout.WorkoutScreen
import com.fitpulse.pro.ui.screens.workout.WorkoutSummaryScreen
import com.fitpulse.pro.ui.theme.FitPulseTheme
import com.fitpulse.pro.ui.theme.FitPulseTypography
import com.fitpulse.pro.viewmodel.FitPulseViewModel

private val bottomBarHiddenRoutes = setOf(
    Screen.Onboarding.route,
    Screen.ActiveWorkout.route,
    Screen.ExercisePicker.route,
    Screen.MeditationTimer.route,
    Screen.BreathingExercise.route,
    Screen.BodyMeasurementLog.route
)

internal fun shouldShowBottomBar(currentRoute: String?): Boolean {
    if (currentRoute == null || currentRoute in bottomBarHiddenRoutes) {
        return false
    }
    return !currentRoute.startsWith("workout_detail")
}

internal fun isBottomNavRouteSelected(screen: Screen, currentRoute: String?): Boolean {
    return when (screen.route) {
        Screen.Learn.route -> {
            currentRoute == screen.route ||
                currentRoute?.startsWith("exercise_library") == true ||
                currentRoute?.startsWith("article/") == true ||
                currentRoute?.startsWith("exercise_detail") == true
        }

        Screen.Workouts.route -> {
            currentRoute == screen.route ||
                currentRoute?.startsWith("workout_detail") == true
        }

        Screen.Home.route -> {
            currentRoute == screen.route ||
                currentRoute == Screen.Profile.route ||
                currentRoute == Screen.Settings.route ||
                currentRoute == Screen.Achievements.route
        }

        else -> currentRoute == screen.route
    }
}

internal fun resolveStartDestination(profile: UserProfile?): String {
    return if (profile?.hasCompletedOnboarding == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }
}

@Composable
fun FitPulseAppScaffold(viewModel: FitPulseViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val showBottomBar = shouldShowBottomBar(currentRoute)
    val colors = FitPulseTheme.colors
    val startDestination = remember(profile?.hasCompletedOnboarding) {
        resolveStartDestination(profile)
    }

    Scaffold(
        containerColor = colors.background,
        bottomBar = {
            if (showBottomBar) {
                FitPulseBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        FitPulseNavHost(
            navController = navController,
            viewModel = viewModel,
            startDestination = startDestination,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) paddingValues.calculateBottomPadding() else 0.dp
            )
        )
    }
}

@Composable
private fun FitPulseNavHost(
    navController: NavHostController,
    viewModel: FitPulseViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToWorkout = { navController.navigate(Screen.Workouts.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                onNavigateToLearn = { navController.navigate(Screen.Learn.route) },
                onStartWorkout = { templateId ->
                    startWorkout(navController, viewModel, templateId)
                },
                onNavigateToArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                }
            )
        }

        composable(Screen.Workouts.route) {
            WorkoutScreen(
                viewModel = viewModel,
                onStartWorkout = { templateId ->
                    startWorkout(navController, viewModel, templateId)
                },
                onViewWorkoutDetail = { workoutId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                },
                onNavigateToExerciseLibrary = {
                    navController.navigate(Screen.ExerciseLibrary.createRoute())
                },
                onResumeWorkout = {
                    navController.navigate(Screen.ActiveWorkout.route)
                }
            )
        }

        composable(Screen.ActiveWorkout.route) {
            ActiveWorkoutScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate(Screen.WorkoutSummary.route) {
                        popUpTo(Screen.Workouts.route)
                    }
                },
                onAddExercise = {
                    navController.navigate(Screen.ExercisePicker.route)
                }
            )
        }

        composable(Screen.WorkoutSummary.route) {
            val context = LocalContext.current
            WorkoutSummaryScreen(
                viewModel = viewModel,
                onDismiss = {
                    viewModel.clearLastFinishedWorkout()
                    navController.popBackStack(Screen.Workouts.route, inclusive = false)
                },
                onShare = {
                    viewModel.shareWorkout(context)
                }
            )
        }

        composable(Screen.ExercisePicker.route) {
            ExerciseLibraryScreen(
                viewModel = viewModel,
                initialMuscleGroup = null,
                onBack = { navController.popBackStack() },
                onExerciseClick = { exercise ->
                    viewModel.addExerciseToWorkout(exercise)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Nutrition.route) {
            NutritionScreen(viewModel = viewModel)
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                viewModel = viewModel,
                onNavigateToMeasurementLog = {
                    navController.navigate(Screen.BodyMeasurementLog.route)
                },
                onNavigateToArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                }
            )
        }

        dialog(Screen.BodyMeasurementLog.route) {
            MeasurementLogRoute(
                viewModel = viewModel,
                onDismiss = { navController.popBackStack() }
            )
        }

        composable(Screen.Learn.route) {
            LearnScreen(
                viewModel = viewModel,
                onNavigateToExerciseLibrary = { muscleGroup ->
                    navController.navigate(Screen.ExerciseLibrary.createRoute(muscleGroup))
                },
                onNavigateToArticle = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                }
            )
        }

        composable(
            route = Screen.ExerciseLibrary.route,
            arguments = listOf(
                navArgument("muscleGroup") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val muscleGroup = backStackEntry.arguments?.getString("muscleGroup")
            ExerciseLibraryScreen(
                viewModel = viewModel,
                initialMuscleGroup = muscleGroup,
                onBack = { navController.popBackStack() },
                onNavigateToExerciseDetail = { exerciseId ->
                    navController.navigate(Screen.ExerciseDetail.createRoute(exerciseId))
                }
            )
        }

        composable(
            route = Screen.ExerciseDetail.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(navArgument("articleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
            ArticleDetailScreen(
                articleId = articleId,
                viewModel = viewModel,
                onNavigateToArticle = { nextArticleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(nextArticleId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Achievements.route) {
            AchievementsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MeditationTimer.route) {
            MeditationScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BreathingExercise.route) {
            BreathingScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutDetailScreen(
                workoutId = workoutId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRepeatWorkout = { id ->
                    viewModel.repeatWorkout(id)
                    navController.navigate(Screen.ActiveWorkout.route) {
                        popUpTo(Screen.Workouts.route)
                    }
                }
            )
        }
    }
}

private fun startWorkout(
    navController: NavHostController,
    viewModel: FitPulseViewModel,
    templateId: Long
) {
    if (templateId == -1L) {
        viewModel.startWorkout()
    } else {
        viewModel.startWorkout(templateId = templateId)
    }
    navController.navigate(Screen.ActiveWorkout.route)
}

@Composable
private fun FitPulseBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val colors = FitPulseTheme.colors
    val configuration = LocalConfiguration.current
    val useCompactLabels = configuration.screenWidthDp < 360 || configuration.fontScale > 1.15f
    NavigationBar(
        containerColor = colors.surface.copy(alpha = 0.95f),
        tonalElevation = 0.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        bottomNavItems.forEach { screen ->
            val isSelected = isBottomNavRouteSelected(screen, currentRoute)
            NavigationBarItem(
                modifier = Modifier.testTag(TestTags.bottomNavItem(screen.route)),
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isSelected) {
                            SelectedIndicator(MaterialTheme.colorScheme)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        androidx.compose.material3.Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.icon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.bottomBarTitle(useCompactLabels),
                        style = if (useCompactLabels) {
                            FitPulseTypography.labelSmall.copy(fontSize = 10.sp)
                        } else {
                            FitPulseTypography.labelSmall
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.bottomBarSelected,
                    selectedTextColor = colors.bottomBarSelected,
                    unselectedIconColor = colors.bottomBarUnselected,
                    unselectedTextColor = colors.bottomBarUnselected,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

private fun Screen.bottomBarTitle(useCompactLabel: Boolean): String {
    if (!useCompactLabel) return title
    return when (this) {
        Screen.Home -> "Home"
        Screen.Workouts -> "Work"
        Screen.Nutrition -> "Food"
        Screen.Progress -> "Stats"
        Screen.Learn -> "Learn"
        else -> title
    }
}

@Composable
private fun SelectedIndicator(colorScheme: ColorScheme) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .width(24.dp)
            .height(3.dp)
            .clip(RoundedCornerShape(1.5.dp))
            .background(colorScheme.primary)
    )
}










