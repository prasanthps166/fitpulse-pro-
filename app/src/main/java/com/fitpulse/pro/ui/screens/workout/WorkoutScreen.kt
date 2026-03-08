package com.fitpulse.pro.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import com.fitpulse.pro.domain.progress.PRDetectionResult
import com.fitpulse.pro.utils.HapticHelper

@Composable
fun WorkoutScreen(
    viewModel: FitPulseViewModel,
    bottomContentPadding: Dp = 0.dp,
    onStartWorkout: (Long) -> Unit,
    onViewWorkoutDetail: (Long) -> Unit,
    onNavigateToExerciseLibrary: () -> Unit,
    onResumeWorkout: () -> Unit
) {
    val templates by viewModel.workoutTemplates.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val totalWorkouts by viewModel.totalWorkoutCount.collectAsState()
    val totalVol by viewModel.totalVolume.collectAsState()
    val totalCals by viewModel.totalCaloriesBurned.collectAsState()
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val quickStartTemplates = remember(templates) { buildQuickStartTemplates(templates).take(6) }
    val configuration = LocalConfiguration.current
    val compactStats = configuration.screenWidthDp < 360 || configuration.fontScale > 1.15f

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .testTag(TestTags.WorkoutScreen)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = FitPulseLayout.ScreenHorizontalPadding,
                    end = FitPulseLayout.ScreenHorizontalPadding,
                    top = FitPulseLayout.ScreenHeaderTopPadding,
                    bottom = 16.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workouts",
                style = FitPulseTypography.displayMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onNavigateToExerciseLibrary,
                modifier = Modifier.clip(CircleShape).background(FitPulseTheme.colors.card)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
            }
        }

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(if (compactStats) 6.dp else 8.dp)
        ) {
            MiniWorkoutStat(
                label = if (compactStats) "Done" else "Workouts",
                value = "$totalWorkouts",
                color = Primary,
                compact = compactStats,
                modifier = Modifier.weight(1f)
            )
            MiniWorkoutStat(
                label = if (compactStats) "Vol" else "Volume",
                value = if (compactStats) {
                    Utils.formatNumber((totalVol ?: 0f).toInt())
                } else {
                    Utils.formatNumber((totalVol ?: 0f).toInt()) + " kg"
                },
                color = Secondary,
                compact = compactStats,
                modifier = Modifier.weight(1f)
            )
            MiniWorkoutStat(label = if (compactStats) "Cals" else "Calories", value = Utils.formatNumber(totalCals ?: 0), color = Accent, compact = compactStats, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        activeWorkout?.let { workout ->
            ResumeWorkoutCard(
                workout = workout,
                onResumeWorkout = onResumeWorkout
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Start New Workout Button
        GradientButton(
            text = "Start New Workout",
            onClick = { onStartWorkout(-1L) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding),
            icon = Icons.Default.PlayArrow
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (quickStartTemplates.isNotEmpty()) {
            SectionHeader(title = "Quick Start")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    EmptyWorkoutQuickStartCard(
                        onClick = { onStartWorkout(-1L) }
                    )
                }
                items(quickStartTemplates, key = { it.id }) { template ->
                    QuickStartTemplateCard(
                        template = template,
                        onClick = { onStartWorkout(template.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Primary,
            divider = { HorizontalDivider(color = Border.copy(alpha = 0.3f)) }
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Templates", color = if (selectedTab == 0) Primary else FitPulseTheme.colors.textTertiary) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("History", color = if (selectedTab == 1) Primary else FitPulseTheme.colors.textTertiary) })
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> TemplatesTab(
                    templates = templates,
                    onStartWorkout = onStartWorkout,
                    modifier = Modifier.fillMaxSize(),
                    bottomContentPadding = bottomContentPadding + 24.dp
                )
                1 -> HistoryTab(
                    workouts = recentWorkouts,
                    onViewDetail = onViewWorkoutDetail,
                    onStartWorkout = onStartWorkout,
                    modifier = Modifier.fillMaxSize(),
                    bottomContentPadding = bottomContentPadding + 24.dp
                )
            }
        }
    }
}

@Composable
private fun ResumeWorkoutCard(
    workout: Workout,
    onResumeWorkout: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Warning.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SaveAs,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Resume In-Progress Workout",
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${workout.name} - ${workout.exercises.orEmpty().size} exercises - started ${Utils.formatTime(workout.startTime)}",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FilledTonalButton(
            onClick = onResumeWorkout,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Warning.copy(alpha = 0.16f),
                contentColor = Warning
            )
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Continue Session", style = FitPulseTypography.labelLarge)
        }
    }
}

@Composable
private fun EmptyWorkoutQuickStartCard(
    onClick: () -> Unit
) {
    GradientCard(
        modifier = Modifier.width(210.dp),
        colors = listOf(Primary.copy(alpha = 0.85f), GradientEnd.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Text(
            text = "Empty Workout",
            style = FitPulseTypography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Start fast and build the session around what is available today.",
            style = FitPulseTypography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickStartPill("Flexible")
            QuickStartPill("From scratch")
        }
    }
}

@Composable
private fun QuickStartTemplateCard(
    template: WorkoutTemplate,
    onClick: () -> Unit
) {
    val accentColor = when (template.difficulty) {
        Difficulty.BEGINNER -> Success
        Difficulty.INTERMEDIATE -> Secondary
        Difficulty.ADVANCED -> Warning
        Difficulty.EXPERT -> Error
    }

    GlassCard(
        modifier = Modifier.width(210.dp),
        onClick = onClick
    ) {
        Text(
            text = template.name,
            style = FitPulseTypography.titleLarge,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = template.description,
            style = FitPulseTypography.bodySmall,
            color = FitPulseTheme.colors.textSecondary,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FitPulseChip(text = template.category, selected = true, color = accentColor)
            FitPulseChip(
                text = template.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                selected = true,
                color = Primary
            )
            FitPulseChip(text = "${template.estimatedDurationMinutes} min", selected = true, color = Primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${template.exercises.size} exercises",
            style = FitPulseTypography.labelMedium,
            color = accentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QuickStartPill(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.16f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            style = FitPulseTypography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MiniWorkoutStat(
    label: String,
    value: String,
    color: Color,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f)),
        border = CardDefaults.outlinedCardBorder().let { BorderStroke(1.dp, color.copy(alpha = 0.2f)) },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 10.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = if (compact) FitPulseTypography.titleLarge else FitPulseTypography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = if (compact) {
                    FitPulseTypography.labelSmall.copy(fontSize = 10.sp)
                } else {
                    FitPulseTypography.labelSmall
                },
                color = FitPulseTheme.colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TemplatesTab(
    templates: List<WorkoutTemplate>,
    onStartWorkout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp
) {
    if (templates.isEmpty()) {
        EmptyState(
            icon = Icons.Default.FitnessCenter,
            title = "No Templates Yet",
            subtitle = "Start a workout or create a custom template",
            actionText = "Start empty workout",
            onAction = { onStartWorkout(-1L) },
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = templates, key = { it.id }) { template ->
                TemplateCard(template = template, onClick = { onStartWorkout(template.id) })
            }
        }
    }
}

@Composable
private fun TemplateCard(template: WorkoutTemplate, onClick: () -> Unit) {
    val accentColor = when (template.difficulty) {
        Difficulty.BEGINNER -> Success
        Difficulty.INTERMEDIATE -> Secondary
        Difficulty.ADVANCED -> Warning
        Difficulty.EXPERT -> Error
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Primary.copy(alpha = 0.3f), GradientEnd.copy(alpha = 0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Primary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(template.name, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
                Text(template.description, style = FitPulseTypography.bodySmall, color = FitPulseTheme.colors.textSecondary, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitPulseChip(text = template.category, selected = true, color = accentColor)
                    FitPulseChip(
                        text = template.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = true,
                        color = Primary
                    )
                    FitPulseChip(text = "${template.estimatedDurationMinutes} min", selected = true, color = Success)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${template.exercises.size} exercises",
                    style = FitPulseTypography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Primary)
        }
    }
}

@Composable
private fun HistoryTab(
    workouts: List<Workout>,
    onViewDetail: (Long) -> Unit,
    onStartWorkout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp
) {
    if (workouts.isEmpty()) {
        EmptyState(
            icon = Icons.Default.History,
            title = "No Workouts Yet",
            subtitle = "Complete your first workout to see it here",
            actionText = "Start workout",
            onAction = { onStartWorkout(-1L) },
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items = workouts, key = { it.id }) { workout ->
                WorkoutSummaryCard(
                    name = workout.name,
                    date = Utils.formatDate(workout.createdAt),
                    duration = Utils.formatDuration(workout.durationMinutes),
                    exercises = (workout.exercises ?: emptyList()).size,
                    volume = Utils.formatWeight(workout.totalVolume),
                    calories = workout.totalCalories,
                    onClick = { onViewDetail(workout.id) }
                )
            }
        }
    }
}

// ============================================================================
// ACTIVE WORKOUT SCREEN
// ============================================================================
@Composable
fun ActiveWorkoutScreen(
    viewModel: FitPulseViewModel,
    onFinish: () -> Unit,
    onAddExercise: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activeWorkout by viewModel.activeWorkout.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val activeExercises = activeWorkout?.exercises.orEmpty()
    val workoutStartTime = activeWorkout?.startTime
    val sessionNotes = activeWorkout?.notes.orEmpty()
    val exerciseCount = activeExercises.size
    var elapsedSeconds by remember(workoutStartTime) {
        mutableIntStateOf(workoutStartTime?.let(::calculateElapsedSeconds) ?: 0)
    }
    var showFinishDialog by remember { mutableStateOf(false) }
    var restTimerSeconds by remember { mutableIntStateOf(0) }
    var restTimerTotal by remember { mutableIntStateOf(0) }
    var isRestTimerRunning by remember { mutableStateOf(false) }

    // PR detection state
    val newPR by viewModel.newPRDetected.collectAsState()
    var showPRToast by remember { mutableStateOf(false) }
    var prToastText by remember { mutableStateOf("") }
    var showConfetti by remember { mutableStateOf(false) }
    val previousPerformanceByExercise = remember(recentWorkouts) {
        buildPreviousExercisePerformanceMap(recentWorkouts)
    }

    LaunchedEffect(newPR) {
        newPR?.let { pr ->
            prToastText = "🏆 New PR! ${pr.exerciseName}\n${pr.weightKg}kg × ${pr.reps} = ~${String.format("%.1f", pr.newOneRM)}kg 1RM"
            showPRToast = true
            showConfetti = true
            // Haptic celebration for PR
            HapticHelper.heavyClick(context)
            viewModel.clearNewPR()
            kotlinx.coroutines.delay(3500)
            showPRToast = false
        }
    }

    // Timer
    LaunchedEffect(workoutStartTime) {
        if (workoutStartTime == null) return@LaunchedEffect
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds = calculateElapsedSeconds(workoutStartTime)
            if (isRestTimerRunning && restTimerSeconds > 0) {
                restTimerSeconds--
                if (restTimerSeconds == 0) {
                    isRestTimerRunning = false
                    // Vibrate when rest timer completes
                    com.fitpulse.pro.utils.ReminderReceiver.triggerRestTimerVibration(context)
                }
            }
        }
    }

    /*
    // Workout Complete + Share dialog
    if (showShareDialog) {
        AlertDialog(
            onDismissRequest = { showShareDialog = false; onFinish() },
            containerColor = FitPulseTheme.colors.surface,
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("🎉", style = FitPulseTypography.displayLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Workout Complete!", style = FitPulseTypography.headlineMedium, color = FitPulseTheme.colors.textPrimary, textAlign = TextAlign.Center)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    val xpGain by viewModel.lastXPGain.collectAsState()
                    if (xpGain != null) {
                        Surface(
                            color = Success.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("⚡ +${xpGain!!.xpGained} XP earned", style = FitPulseTypography.titleMedium, color = Success)
                                if (xpGain!!.didLevelUp) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("🎉 Level Up!", style = FitPulseTypography.titleMedium, color = Warning)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Share your accomplishment!", style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textSecondary)
                }
            },
            confirmButton = {
                GradientButton(
                    text = "Share Workout 📤",
                    onClick = {
                        viewModel.shareWorkout(context)
                    },
                    icon = Icons.Default.Share
                )
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false; onFinish() }) {
                    Text("Done", color = FitPulseTheme.colors.textTertiary)
                }
            }
        )
    }

    */
    if (showFinishDialog) {
        FinishWorkoutDialog(
            onDismiss = { showFinishDialog = false },
            currentNotes = sessionNotes,
            onUpdateNotes = viewModel::updateActiveWorkoutNotes,
            onFinish = { rating, mood ->
                viewModel.finishWorkout(rating, mood, activeWorkout?.notes.orEmpty())
                showFinishDialog = false
                onFinish()
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activeWorkout?.name ?: "Workout",
                    style = FitPulseTypography.headlineLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Utils.formatDurationSeconds(elapsedSeconds),
                    style = FitPulseTypography.displaySmall,
                    color = Primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FitPulseChip(text = "Active Session", selected = true, color = Primary)
                    Text(
                        text = "${activeWorkout?.exercises.orEmpty().size} exercises",
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                onClick = {
                    viewModel.cancelWorkout()
                    onFinish()
                },
                shape = RoundedCornerShape(14.dp),
                color = Error.copy(alpha = 0.16f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Error,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Cancel",
                        style = FitPulseTypography.labelLarge,
                        color = Error
                    )
                }
            }
        }

        // ====== ENHANCED REST TIMER ======
        AnimatedVisibility(
            visible = isRestTimerRunning || restTimerSeconds > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.9f)),
                border = BorderStroke(1.dp, Warning.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("REST TIMER", style = FitPulseTypography.labelMedium, color = Warning, letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Circular progress ring
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                        val progress = if (restTimerTotal > 0) restTimerSeconds.toFloat() / restTimerTotal else 0f
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = tween(300),
                            label = "rest"
                        )
                        
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 6.dp,
                            color = Border.copy(alpha = 0.2f),
                            trackColor = Color.Transparent
                        )
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 6.dp,
                            color = Warning,
                            trackColor = Color.Transparent
                        )
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                Utils.formatDurationSeconds(restTimerSeconds),
                                style = FitPulseTypography.displaySmall,
                                color = Warning,
                                fontWeight = FontWeight.Bold
                            )
                            Text("remaining", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Quick adjust buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalButton(
                            onClick = { restTimerSeconds = maxOf(0, restTimerSeconds - 15); restTimerTotal = maxOf(restTimerTotal, restTimerSeconds) },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Border.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("-15s", color = FitPulseTheme.colors.textSecondary, style = FitPulseTypography.labelMedium)
                        }
                        
                        FilledTonalButton(
                            onClick = { isRestTimerRunning = false; restTimerSeconds = 0 },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Warning.copy(alpha = 0.2f),
                                contentColor = Warning
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skip", style = FitPulseTypography.labelMedium)
                        }
                        
                        FilledTonalButton(
                            onClick = { restTimerSeconds += 15; restTimerTotal += 15 },
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Border.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("+15s", color = FitPulseTheme.colors.textSecondary, style = FitPulseTypography.labelMedium)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Quick rest timer presets (when timer is NOT running)
        if (!isRestTimerRunning && restTimerSeconds == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30 to "30s", 60 to "1m", 90 to "1:30", 120 to "2m", 180 to "3m").forEach { (secs, label) ->
                    FilledTonalButton(
                        onClick = {
                            restTimerSeconds = secs
                            restTimerTotal = secs
                            isRestTimerRunning = true
                        },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Warning.copy(alpha = 0.1f),
                            contentColor = Warning
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(label, style = FitPulseTypography.labelSmall)
                    }
                }
            }
        }

        activeExercises.firstOrNull()?.let { firstExercise ->
            WarmUpGuidanceCard(
                firstExerciseName = firstExercise.exerciseName,
                exerciseCount = exerciseCount
            )
        }

        // Exercise List
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 156.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                items = activeExercises,
                key = { _, workoutExercise -> "${workoutExercise.exerciseId}_${workoutExercise.orderIndex}" }
            ) { exIndex, workoutExercise ->
                ActiveExerciseCard(
                    workoutExercise = workoutExercise,
                    previousPerformance = previousPerformanceByExercise[workoutExercise.exerciseId],
                    substitutionCandidates = remember(workoutExercise.exerciseId, allExercises) {
                        buildExerciseSubstitutions(workoutExercise, allExercises)
                    },
                    onUpdateExercise = { updated ->
                        viewModel.updateWorkoutExercise(exIndex, updated)
                    },
                    onStartRest = { seconds ->
                        restTimerSeconds = seconds
                        restTimerTotal = seconds
                        isRestTimerRunning = true
                    },
                    onPRDetect = { exerciseId, exerciseName, weightKg, reps ->
                        viewModel.detectAndStorePR(exerciseId, exerciseName, weightKg, reps)
                    }
                )
            }

            // Inline workout notes
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Primary.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Session Notes",
                                style = FitPulseTypography.titleMedium,
                                color = FitPulseTheme.colors.textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Saved with this workout so you can review cues later.",
                                style = FitPulseTypography.bodySmall,
                                color = FitPulseTheme.colors.textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sessionNotes,
                        onValueChange = viewModel::updateActiveWorkoutNotes,
                        placeholder = {
                            Text(
                                "Log form cues, pain notes, wins, or what to adjust next time.",
                                color = FitPulseTheme.colors.textTertiary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Border.copy(alpha = 0.4f),
                            focusedTextColor = FitPulseTheme.colors.textPrimary,
                            unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                            cursorColor = Primary
                        ),
                        maxLines = 3
                    )
                }
            }
        }
    }

    ActiveWorkoutActionDock(
        exerciseCount = exerciseCount,
        onAddExercise = onAddExercise,
        onFinish = { showFinishDialog = true },
        modifier = Modifier.align(Alignment.BottomCenter)
    )

    // Confetti overlay for PR
    if (showConfetti) {
        ConfettiOverlay(
            isActive = true,
            onComplete = { showConfetti = false }
        )
    }

    // PR Toast
    AnimatedVisibility(
        visible = showPRToast,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 132.dp),
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🏆", style = FitPulseTypography.headlineMedium)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    prToastText,
                    style = FitPulseTypography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    } // end Box
}




@Composable
private fun ActiveWorkoutActionDock(
    exerciseCount: Int,
    onAddExercise: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.surface.copy(alpha = 0.98f)),
        border = BorderStroke(1.dp, Border.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (exerciseCount == 1) "1 exercise in progress" else "$exerciseCount exercises in progress",
                style = FitPulseTypography.labelLarge,
                color = FitPulseTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = onAddExercise,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Secondary.copy(alpha = 0.14f),
                        contentColor = Secondary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Exercise", style = FitPulseTypography.labelLarge)
                }

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Success,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Finish", style = FitPulseTypography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun ActiveExerciseCard(
    workoutExercise: WorkoutExercise,
    previousPerformance: PreviousExercisePerformance?,
    substitutionCandidates: List<Exercise>,
    onUpdateExercise: (WorkoutExercise) -> Unit,
    onStartRest: (Int) -> Unit,
    onPRDetect: (Long, String, Float, Int) -> Unit = { _, _, _, _ -> }
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // Exercise name + note toggle
        var showNotes by remember { mutableStateOf(workoutExercise.notes?.isNotBlank() == true) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                workoutExercise.exerciseName,
                style = FitPulseTypography.headlineSmall,
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showNotes = !showNotes },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    if (showNotes) Icons.Default.EditNote else Icons.AutoMirrored.Filled.NoteAdd,
                    contentDescription = "Notes",
                    tint = if (showNotes) Primary else FitPulseTheme.colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Inline exercise notes
        AnimatedVisibility(visible = showNotes) {
            var noteText by remember { mutableStateOf(workoutExercise.notes ?: "") }
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it; onUpdateExercise(workoutExercise.copy(notes = it)) },
                placeholder = { Text("Exercise notes...", color = FitPulseTheme.colors.textTertiary, style = FitPulseTypography.bodySmall) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                textStyle = FitPulseTypography.bodySmall.copy(color = FitPulseTheme.colors.textPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = Border.copy(alpha = 0.3f),
                    cursorColor = Primary
                )
            )
        }

        previousPerformance?.let { performance ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Text(
                        text = "Last logged best: ${performance.displayShort}",
                        style = FitPulseTypography.labelLarge,
                        color = FitPulseTheme.colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = buildProgressionCue(performance, workoutExercise.sets.orEmpty()),
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (canSubstituteExercise(workoutExercise) && substitutionCandidates.isNotEmpty()) {
            Text(
                text = "Quick swaps if equipment is busy",
                style = FitPulseTypography.labelMedium,
                color = FitPulseTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(substitutionCandidates, key = { it.id }) { substitute ->
                    FitPulseChip(
                        text = substitute.name,
                        selected = false,
                        color = Secondary,
                        onClick = {
                            onUpdateExercise(
                                applyExerciseSubstitution(
                                    workoutExercise = workoutExercise,
                                    substitute = substitute
                                )
                            )
                        }
                    )
                }
            }
        } else if (workoutExercise.sets.orEmpty().any { it.isCompleted }) {
            Text(
                text = "Swap before your first completed set to keep this log accurate.",
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textTertiary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Set Headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SET", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.width(40.dp))
            Text("PREV", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.width(50.dp))
            Text("KG", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.weight(1f))
            Text("REPS", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.weight(1f))
            Text("✓", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.width(40.dp))
        }

        val safeSets = workoutExercise.sets ?: emptyList()
        safeSets.forEachIndexed { setIndex, set ->
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Set number + PR badge
                Column(
                    modifier = Modifier.width(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (set.isWarmup) "W${warmupIndexForSet(safeSets, setIndex)}" else "${set.setNumber}",
                        style = FitPulseTypography.titleMedium,
                        color = when {
                            set.isWarmup -> Warning
                            set.isCompleted -> Success
                            else -> FitPulseTheme.colors.textSecondary
                        }
                    )
                    if (set.isPersonalRecord) {
                        Text("🏆", style = FitPulseTypography.labelSmall)
                    }
                }
                Text(
                    previousPerformance?.displayShort ?: "-",
                    style = FitPulseTypography.bodySmall,
                    color = if (set.isWarmup) Warning else FitPulseTheme.colors.textTertiary,
                    modifier = Modifier.width(50.dp)
                )

                // Weight input
                var weightText by remember(set.weightKg) { mutableStateOf(if (set.weightKg > 0) set.weightKg.toString() else "") }
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { newVal ->
                        weightText = newVal.filter { it.isDigit() || it == '.' }
                        val weight = newVal.toFloatOrNull() ?: 0f
                        val updatedSets = safeSets.toMutableList()
                        updatedSets[setIndex] = set.copy(weightKg = weight)
                        onUpdateExercise(workoutExercise.copy(sets = updatedSets))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 2.dp),
                    textStyle = FitPulseTypography.bodyMedium.copy(color = FitPulseTheme.colors.textPrimary),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border.copy(alpha = 0.5f),
                        cursorColor = Primary
                    )
                )

                // Reps input
                var repsText by remember(set.reps) { mutableStateOf(if (set.reps > 0) set.reps.toString() else "") }
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { newVal ->
                        repsText = newVal.filter { it.isDigit() }
                        val reps = newVal.toIntOrNull() ?: 0
                        val updatedSets = safeSets.toMutableList()
                        updatedSets[setIndex] = set.copy(reps = reps)
                        onUpdateExercise(workoutExercise.copy(sets = updatedSets))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(horizontal = 2.dp),
                    textStyle = FitPulseTypography.bodyMedium.copy(color = FitPulseTheme.colors.textPrimary),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border.copy(alpha = 0.5f),
                        cursorColor = Primary
                    )
                )

                // Complete button
                IconButton(
                    onClick = {
                        val nowCompleting = !set.isCompleted
                        val updatedSets = safeSets.toMutableList()
                        updatedSets[setIndex] = set.copy(isCompleted = nowCompleting)
                        onUpdateExercise(workoutExercise.copy(sets = updatedSets))
                        if (nowCompleting) {
                            onStartRest(if (set.isWarmup) minOf(45, workoutExercise.restSeconds) else workoutExercise.restSeconds)
                            // Trigger PR check
                            if (!set.isWarmup && set.weightKg > 0 && set.reps > 0) {
                                onPRDetect(workoutExercise.exerciseId, workoutExercise.exerciseName, set.weightKg, set.reps)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (set.isCompleted) Success.copy(alpha = 0.2f) else Border.copy(alpha = 0.2f))
                ) {
                    Icon(
                        if (set.isCompleted) Icons.Default.Check else Icons.Default.Circle,
                        contentDescription = null,
                        tint = if (set.isCompleted) Success else FitPulseTheme.colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(
                onClick = {
                    onUpdateExercise(workoutExercise.copy(sets = addWarmupSet(safeSets)))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Warning, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Warm-Up Set", color = Warning, style = FitPulseTypography.labelMedium)
            }

            TextButton(
                onClick = {
                    onUpdateExercise(workoutExercise.copy(sets = addWorkingSet(safeSets)))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set", color = Primary, style = FitPulseTypography.labelMedium)
            }
        }
    }
}

@Composable
private fun WarmUpGuidanceCard(
    firstExerciseName: String,
    exerciseCount: Int
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Warning.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Warm up before the first hard set",
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Use 1-3 lighter sets for $firstExerciseName, then move into your working weight. You have $exerciseCount exercises planned today.",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
        }
    }
}

private fun addWorkingSet(existingSets: List<ExerciseSet>): List<ExerciseSet> {
    val workingSetCount = existingSets.count { !it.isWarmup }
    return renumberSets(
        existingSets + ExerciseSet(
            setNumber = workingSetCount + 1,
            isWarmup = false
        )
    )
}

private fun addWarmupSet(existingSets: List<ExerciseSet>): List<ExerciseSet> {
    val firstWorkingIndex = existingSets.indexOfFirst { !it.isWarmup }
    val updatedSets = existingSets.toMutableList()
    val insertIndex = if (firstWorkingIndex >= 0) firstWorkingIndex else existingSets.size
    updatedSets.add(
        insertIndex,
        ExerciseSet(
            setNumber = 0,
            isWarmup = true
        )
    )
    return renumberSets(updatedSets)
}

private fun renumberSets(sets: List<ExerciseSet>): List<ExerciseSet> {
    var workingSetNumber = 1
    return sets.map { set ->
        if (set.isWarmup) {
            set.copy(setNumber = 0)
        } else {
            set.copy(setNumber = workingSetNumber++)
        }
    }
}

private fun warmupIndexForSet(
    sets: List<ExerciseSet>,
    setIndex: Int
): Int {
    return sets
        .take(setIndex + 1)
        .count { it.isWarmup }
        .coerceAtLeast(1)
}

private fun calculateElapsedSeconds(startTimeMillis: Long): Int {
    return ((System.currentTimeMillis() - startTimeMillis) / 1000L)
        .coerceAtLeast(0L)
        .toInt()
}





@Composable
private fun FinishWorkoutDialog(
    onDismiss: () -> Unit,
    currentNotes: String,
    onUpdateNotes: (String) -> Unit,
    onFinish: (Int, WorkoutMood?) -> Unit
) {
    var rating by remember { mutableIntStateOf(3) }
    var selectedMood by remember { mutableStateOf<WorkoutMood?>(null) }
    var notes by remember(currentNotes) { mutableStateOf(currentNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FitPulseTheme.colors.surface,
        title = {
            Text("Finish Workout", style = FitPulseTypography.headlineMedium, color = FitPulseTheme.colors.textPrimary)
        },
        text = {
            Column {
                Text("How was your workout?", style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textSecondary)
                Spacer(modifier = Modifier.height(12.dp))

                // Rating stars
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (star <= rating) Warning else FitPulseTheme.colors.textTertiary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                /*
                // Mood selection
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkoutMood.values().forEach { mood ->
                        val emoji = when (mood) {
                            WorkoutMood.GREAT -> "🔥"
                            WorkoutMood.GOOD -> "💪"
                            WorkoutMood.OKAY -> "👍"
                            WorkoutMood.TIRED -> "😮‍💨"
                            WorkoutMood.TERRIBLE -> "😵"
                        }
                        Surface(
                            shape = CircleShape,
                            color = if (selectedMood == mood) Primary.copy(alpha = 0.2f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selectedMood == mood) Primary else Border),
                            onClick = { selectedMood = mood },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, style = FitPulseTypography.headlineSmall)
                            }
                        }
                    }
                }

                */
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WorkoutMood.values().forEach { mood ->
                        val moodLabel = when (mood) {
                            WorkoutMood.GREAT -> "Great"
                            WorkoutMood.GOOD -> "Good"
                            WorkoutMood.OKAY -> "Okay"
                            WorkoutMood.TIRED -> "Tired"
                            WorkoutMood.TERRIBLE -> "Low"
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (selectedMood == mood) Primary.copy(alpha = 0.2f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selectedMood == mood) Primary else Border),
                            onClick = { selectedMood = mood },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = moodLabel,
                                    style = FitPulseTypography.labelMedium,
                                    color = if (selectedMood == mood) Primary else FitPulseTheme.colors.textSecondary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = {
                        notes = it
                        onUpdateNotes(it)
                    },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border,
                        focusedTextColor = FitPulseTheme.colors.textPrimary,
                        unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                        cursorColor = Primary,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = FitPulseTheme.colors.textTertiary
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            GradientButton(
                text = "Save Workout",
                onClick = { onFinish(rating, selectedMood) }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FitPulseTheme.colors.textTertiary)
            }
        }
    )
}


