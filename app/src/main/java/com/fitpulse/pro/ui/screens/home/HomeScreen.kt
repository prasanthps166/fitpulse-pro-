package com.fitpulse.pro.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.domain.home.HomeFocusAction
import com.fitpulse.pro.domain.home.HomeFocusMetric
import com.fitpulse.pro.domain.home.HomeFocusState
import com.fitpulse.pro.domain.home.buildHomeFocusState
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.HapticHelper
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.utils.XPManager
import com.fitpulse.pro.viewmodel.FitPulseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: FitPulseViewModel,
    onNavigateToWorkout: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToLearn: () -> Unit,
    onStartWorkout: (Long) -> Unit,
    onNavigateToArticle: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val profile by viewModel.userProfile.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()
    val todayCalories by viewModel.todayCalories.collectAsState()
    val todayWater by viewModel.todayWater.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val templates by viewModel.workoutTemplates.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val unlockedCount by viewModel.unlockedAchievementCount.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val xpProgress by viewModel.xpProgress.collectAsState()
    val totalXP by viewModel.totalXP.collectAsState()
    val lastXPGain by viewModel.lastXPGain.collectAsState()
    val articles = viewModel.articles
    val featuredArticles = remember(articles) {
        val featuredArticleOrder = listOf(
            "fitness_fundamentals_full_guide",
            "fitness_dos_and_donts_full_guide",
            "common_fitness_myths_full_guide",
            "strength_starter_plan",
            "protein_macros_muscle_fat_loss"
        )

        articles.sortedWith(
            compareBy<FitnessArticle> {
                featuredArticleOrder.indexOf(it.id).let { index ->
                    if (index == -1) Int.MAX_VALUE else index
                }
            }.thenBy { it.title }
        ).take(4)
    }
    val quickStartTemplates = remember(templates) { templates.take(5) }

    val greeting = Utils.getGreeting()
    val userName = profile?.name?.ifBlank { "Athlete" } ?: "Athlete"
    val waterGoalMl = profile?.dailyWaterGoalMl ?: 3000
    val homeFocus = remember(
        activeWorkout,
        todayStats,
        waterGoalMl,
        recentWorkouts,
        templates,
        featuredArticles
    ) {
        buildHomeFocusState(
            activeWorkout = activeWorkout,
            todayStats = todayStats,
            waterGoalMl = waterGoalMl,
            recentWorkouts = recentWorkouts,
            templates = templates,
            articles = featuredArticles
        )
    }

    // Level-up confetti
    var showConfetti by remember { mutableStateOf(false) }
    var showXPToast by remember { mutableStateOf(false) }
    var xpToastText by remember { mutableStateOf("") }

    LaunchedEffect(lastXPGain) {
        lastXPGain?.let { gain ->
            if (gain.didLevelUp) {
                showConfetti = true
                HapticHelper.celebration(context)
            } else {
                HapticHelper.doublePulse(context)
            }
            xpToastText = "+${gain.xpGained} XP"
            showXPToast = true
            kotlinx.coroutines.delay(2500)
            showXPToast = false
            viewModel.clearLastXPGain()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FitPulseTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .verticalScroll(rememberScrollState())
                .padding(bottom = FitPulseLayout.ScreenBottomPadding)
                .testTag(TestTags.HomeScreen)
        ) {
        // ====== TOP BAR ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = greeting,
                    style = FitPulseTypography.bodyMedium,
                    color = FitPulseTheme.colors.textSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = FitPulseTypography.displaySmall,
                        color = FitPulseTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Primary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PRO",
                            style = FitPulseTypography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToAchievements,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FitPulseTheme.colors.card)
                ) {
                    BadgedBox(
                        badge = {
                            if (unlockedCount > 0) {
                                Badge(containerColor = Accent) {
                                    Text("$unlockedCount", style = FitPulseTypography.badge)
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.EmojiEvents, contentDescription = "Achievements", tint = Warning)
                    }
                }
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(FitPulseTheme.colors.card)
                ) {
                    Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TodayFocusCard(
            focus = homeFocus,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .testTag(TestTags.HomeFocusCard),
            onAction = {
                when (homeFocus.action) {
                    HomeFocusAction.RESUME_WORKOUT -> onNavigateToWorkout()
                    HomeFocusAction.START_WORKOUT -> onStartWorkout(homeFocus.suggestedTemplateId ?: -1L)
                    HomeFocusAction.LOG_WATER -> viewModel.addWater(250)
                    HomeFocusAction.OPEN_KNOWLEDGE -> {
                        homeFocus.suggestedArticleId?.let(onNavigateToArticle) ?: onNavigateToLearn()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====== XP LEVEL CARD ======
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level badge
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Primary.copy(alpha = 0.4f), GradientEnd.copy(alpha = 0.3f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = XPManager.rankEmoji(currentLevel),
                        style = FitPulseTypography.headlineMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Level $currentLevel",
                            style = FitPulseTypography.titleLarge,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = XPManager.rankTitle(currentLevel),
                                style = FitPulseTypography.labelSmall,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // XP Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Border.copy(alpha = 0.3f))
                    ) {
                        val animatedXP by animateFloatAsState(
                            targetValue = xpProgress.coerceIn(0f, 1f),
                            animationSpec = tween(800),
                            label = "xp"
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedXP)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    brush = Brush.linearGradient(listOf(Primary, GradientEnd))
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${Utils.formatNumber(totalXP)} XP total - ${viewModel.xpManager.getXPToNextLevel()} XP to next level",
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textTertiary
                    )
                }
            }
            // Show XP gain notification
            if (lastXPGain != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Success.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Success,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "+${lastXPGain!!.xpGained} XP" +
                                if (lastXPGain!!.didLevelUp) " - Level up reached" else "",
                            style = FitPulseTypography.labelMedium,
                            color = Success
                        )
                    }
                }
                LaunchedEffect(lastXPGain) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearLastXPGain()
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (featuredArticles.isNotEmpty()) {
            SectionHeader(
                title = "Knowledge Library",
                actionText = "View All",
                onAction = onNavigateToLearn
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = featuredArticles, key = { it.id }) { article ->
                    FeaturedKnowledgeCard(
                        article = article,
                        onClick = { onNavigateToArticle(article.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ====== TODAY'S STATS ======
        SectionHeader(title = "Today's Summary")

        // Calorie ring + stats grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag(TestTags.HomeSummarySection),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Calorie Ring
            GlassCard(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val calorieGoal = profile?.dailyCalorieGoal ?: 2000
                    val caloriesConsumed = todayCalories ?: 0
                    val progress = if (calorieGoal > 0) caloriesConsumed.toFloat() / calorieGoal else 0f

                    CircularProgressRing(
                        progress = progress,
                        size = 120.dp,
                        strokeWidth = 12.dp,
                        progressColor = Primary,
                        secondaryProgressColor = GradientEnd
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnimatedCounter(
                                count = caloriesConsumed,
                                style = FitPulseTypography.headlineLarge
                            )
                            Text(
                                "/ $calorieGoal",
                                style = FitPulseTypography.bodySmall,
                                color = FitPulseTheme.colors.textTertiary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Calories", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textSecondary)
                }
            }

            // Stats column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MiniStatCard(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    value = "${todayStats?.steps ?: 0}",
                    label = "Steps",
                    color = GradientCyan
                )
                MiniStatCard(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${todayStats?.caloriesBurned ?: 0}",
                    label = "Burned",
                    color = Accent
                )
                MiniStatCard(
                    icon = Icons.Default.Timer,
                    value = "${todayStats?.activeMinutes ?: 0}m",
                    label = "Active",
                    color = Success
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Water intake
            WaterIntakeCard(
                current = todayWater ?: 0,
                goal = waterGoalMl,
                onAddWater = { viewModel.addWater(250) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .testTag(TestTags.HomeWaterCard)
            )

        Spacer(modifier = Modifier.height(20.dp))

        // ====== QUICK START ======
        SectionHeader(title = "Quick Start", actionText = "All Templates", onAction = onNavigateToWorkout)

            LazyRow(
                modifier = Modifier.testTag(TestTags.HomeQuickStartSection),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Quick start empty workout
            item {
                QuickStartCard(
                    title = "Empty Workout",
                    subtitle = "Start from scratch",
                    icon = Icons.Default.Add,
                    gradientColors = listOf(Primary, GradientMiddle),
                    onClick = { onStartWorkout(-1L) }
                )
            }
            items(items = quickStartTemplates, key = { it.id }) { template ->
                QuickStartCard(
                    title = template.name,
                    subtitle = "${template.exercises.size} exercises - ${template.estimatedDurationMinutes} min",
                    icon = Icons.Default.FitnessCenter,
                    gradientColors = when (template.name) {
                        "Push Day" -> listOf(Accent, Warning)
                        "Pull Day" -> listOf(Secondary, Info)
                        "Leg Day" -> listOf(Success, GradientCyan)
                        "Full Body Blast" -> listOf(GradientMiddle, Primary)
                        "HIIT Cardio" -> listOf(Error, Accent)
                        else -> listOf(Primary, Secondary)
                    },
                    onClick = { onStartWorkout(template.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ====== RECENT WORKOUTS ======
        SectionHeader(title = "Recent Workouts", actionText = "See All", onAction = onNavigateToWorkout)
        if (recentWorkouts.isNotEmpty()) {
            recentWorkouts.take(3).forEach { workout ->
                WorkoutSummaryCard(
                    name = workout.name,
                    date = Utils.formatDate(workout.createdAt),
                    duration = Utils.formatDuration(workout.durationMinutes),
                    exercises = (workout.exercises ?: emptyList()).size,
                    volume = Utils.formatWeight(workout.totalVolume),
                    calories = workout.totalCalories,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        } else {
            EmptyState(
                icon = Icons.Default.FitnessCenter,
                title = "No workouts logged yet",
                subtitle = "Start one session today and your history will show up here.",
                actionText = "Start workout",
                onAction = { onStartWorkout(homeFocus.suggestedTemplateId ?: -1L) },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .testTag(TestTags.HomeRecentWorkoutsEmptyState)
            )
        }

        // ====== DAILY STREAK ======
        Spacer(modifier = Modifier.height(20.dp))
        StreakCard(
            currentStreak = todayStats?.streakDays ?: 0,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    } // end Column

    // Confetti overlay
    if (showConfetti) {
        ConfettiOverlay(
            isActive = true,
            onComplete = { showConfetti = false }
        )
    }

    // XP toast
    AnimatedVisibility(
        visible = showXPToast,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 120.dp),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Surface(
            color = Primary.copy(alpha = 0.9f),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(xpToastText, style = FitPulseTypography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
    } // end Box
}

// ============================================================================
// SUB-COMPONENTS
// ============================================================================
@Composable
private fun TodayFocusCard(
    focus: HomeFocusState,
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val compactMetrics = configuration.screenWidthDp < 360 || configuration.fontScale > 1.15f
    val accentColor = when (focus.action) {
        HomeFocusAction.RESUME_WORKOUT -> Warning
        HomeFocusAction.START_WORKOUT -> Primary
        HomeFocusAction.LOG_WATER -> Secondary
        HomeFocusAction.OPEN_KNOWLEDGE -> Info
    }

    GradientCard(
        modifier = modifier.fillMaxWidth(),
        colors = listOf(accentColor.copy(alpha = 0.45f), GradientMiddle.copy(alpha = 0.22f))
    ) {
        Text(
            text = "Today's Focus",
            style = FitPulseTypography.labelLarge,
            color = FitPulseTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = focus.title,
            style = FitPulseTypography.headlineSmall,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = focus.subtitle,
            style = FitPulseTypography.bodyMedium,
            color = FitPulseTheme.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(14.dp))
        if (compactMetrics) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FocusMetricChip(
                    metric = focus.primaryMetric,
                    color = accentColor,
                    modifier = Modifier.fillMaxWidth()
                )
                FocusMetricChip(
                    metric = focus.secondaryMetric,
                    color = FitPulseTheme.colors.textSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FocusMetricChip(
                    metric = focus.primaryMetric,
                    color = accentColor,
                    modifier = Modifier.weight(1f)
                )
                FocusMetricChip(
                    metric = focus.secondaryMetric,
                    color = FitPulseTheme.colors.textSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        FilledTonalButton(
            onClick = onAction,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = accentColor.copy(alpha = 0.18f),
                contentColor = accentColor
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = focus.actionLabel, style = FitPulseTypography.labelLarge)
        }
    }
}

@Composable
private fun FocusMetricChip(
    metric: HomeFocusMetric,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = metric.label,
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.value,
                style = FitPulseTypography.titleMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MiniStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f)),
        border = CardDefaults.outlinedCardBorder().let {
            androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.2f))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(value, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                Text(label, style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary)
            }
        }
    }
}

@Composable
private fun WaterIntakeCard(
    current: Int,
    goal: Int,
    onAddWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) current.toFloat() / goal else 0f
    val glasses = current / 250
    val hydrationSummary = buildString {
        append("$glasses glasses today")
        if (goal > 0) {
            val delta = goal - current
            append(" - ")
            append(
                if (delta >= 0) {
                    "${Utils.formatHydrationAmount(delta)} left"
                } else {
                    "${Utils.formatHydrationAmount(-delta)} over goal"
                }
            )
        }
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Secondary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Water Intake", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textTertiary)
                    Text(
                        "${Utils.formatHydrationAmount(current)} / ${Utils.formatHydrationAmount(goal)}",
                        style = FitPulseTypography.titleLarge,
                        color = FitPulseTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        hydrationSummary,
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            FilledTonalButton(
                onClick = onAddWater,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Secondary.copy(alpha = 0.2f),
                    contentColor = Secondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                modifier = Modifier.defaultMinSize(minWidth = 92.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "+250ml",
                    style = FitPulseTypography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Border.copy(alpha = 0.3f))
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = progress.coerceIn(0f, 1f),
                animationSpec = tween(800),
                label = "water"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.linearGradient(listOf(Secondary, GradientCyan))
                    )
            )
        }
    }
}

@Composable
private fun QuickStartCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.3f) }
                    )
                )
                .border(
                    1.dp,
                    gradientColors.first().copy(alpha = 0.3f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(gradientColors.first().copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = gradientColors.first(), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    title,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    subtitle,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FeaturedKnowledgeCard(
    article: FitnessArticle,
    onClick: () -> Unit
) {
    val categoryColor = article.category.homeAccentColor()

    GlassCard(
        modifier = Modifier
            .width(240.dp)
            .height(148.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                .background(categoryColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = article.category.homeIcon(),
                    contentDescription = null,
                    tint = categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            FitPulseChip(
                text = article.category.homeDisplayName(),
                selected = true,
                color = categoryColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = article.title,
            style = FitPulseTypography.titleLarge,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = article.quickTakeaway,
            style = FitPulseTypography.bodySmall,
            color = FitPulseTheme.colors.textSecondary,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Updated ${article.lastUpdated} - ${article.readTimeMinutes} min",
            style = FitPulseTypography.labelSmall,
            color = FitPulseTheme.colors.textTertiary
        )
    }
}

private fun ArticleCategory.homeDisplayName(): String = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> "Strength Training"
    ArticleCategory.HYPERTROPHY -> "Hypertrophy"
    ArticleCategory.ENDURANCE -> "Endurance"
    ArticleCategory.MOBILITY -> "Mobility"
    ArticleCategory.NUTRITION -> "Nutrition"
    ArticleCategory.RECOVERY -> "Recovery"
    ArticleCategory.INJURY_PREVENTION -> "Injury Prevention"
    ArticleCategory.SPECIAL_POPULATIONS -> "Special Populations"
    ArticleCategory.MENTAL_HEALTH -> "Mental Health"
    ArticleCategory.TRENDS_SCIENCE -> "Trends & Science"
}

private fun ArticleCategory.homeAccentColor(): Color = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> Primary
    ArticleCategory.HYPERTROPHY -> GradientMiddle
    ArticleCategory.ENDURANCE -> Info
    ArticleCategory.MOBILITY -> Accent
    ArticleCategory.NUTRITION -> Success
    ArticleCategory.RECOVERY -> Secondary
    ArticleCategory.INJURY_PREVENTION -> Warning
    ArticleCategory.SPECIAL_POPULATIONS -> Accent
    ArticleCategory.MENTAL_HEALTH -> Warning
    ArticleCategory.TRENDS_SCIENCE -> Info
}

private fun ArticleCategory.homeIcon(): ImageVector = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> Icons.Default.FitnessCenter
    ArticleCategory.HYPERTROPHY -> Icons.AutoMirrored.Filled.TrendingUp
    ArticleCategory.ENDURANCE -> Icons.AutoMirrored.Filled.DirectionsRun
    ArticleCategory.MOBILITY -> Icons.Default.SelfImprovement
    ArticleCategory.NUTRITION -> Icons.Default.Restaurant
    ArticleCategory.RECOVERY -> Icons.Default.Hotel
    ArticleCategory.INJURY_PREVENTION -> Icons.Default.HealthAndSafety
    ArticleCategory.SPECIAL_POPULATIONS -> Icons.Default.School
    ArticleCategory.MENTAL_HEALTH -> Icons.Default.Psychology
    ArticleCategory.TRENDS_SCIENCE -> Icons.Default.Science
}

@Composable
private fun MindfulnessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
            Text(subtitle, style = FitPulseTypography.bodySmall, color = FitPulseTheme.colors.textSecondary)
        }
    }
}

@Composable
private fun ChallengeCard(
    challenge: Challenge,
    modifier: Modifier = Modifier
) {
    val progress = if (challenge.target > 0) challenge.currentProgress.toFloat() / challenge.target else 0f

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressRing(
                progress = progress,
                size = 48.dp,
                strokeWidth = 5.dp,
                progressColor = Warning
            ) {
                Text(
                    "${(progress * 100).toInt()}%",
                    style = FitPulseTypography.labelSmall,
                    color = Warning
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(challenge.name, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
                Text(challenge.description, style = FitPulseTypography.bodySmall, color = FitPulseTheme.colors.textSecondary, maxLines = 1)
            }
            Text(
                "${challenge.currentProgress}/${challenge.target}",
                style = FitPulseTypography.labelMedium,
                color = FitPulseTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    GradientCard(
        modifier = modifier.fillMaxWidth(),
        colors = listOf(Warning.copy(alpha = 0.4f), Accent.copy(alpha = 0.3f))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Daily Streak", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textSecondary)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$currentStreak",
                        style = FitPulseTypography.statValue,
                        color = FitPulseTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "days",
                        style = FitPulseTypography.titleMedium,
                        color = FitPulseTheme.colors.textSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


