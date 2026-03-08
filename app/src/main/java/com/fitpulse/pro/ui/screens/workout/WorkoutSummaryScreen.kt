package com.fitpulse.pro.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.components.GlassCard
import com.fitpulse.pro.ui.components.GradientButton
import com.fitpulse.pro.ui.components.GradientCard
import com.fitpulse.pro.ui.components.FitPulseChip
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import kotlinx.coroutines.delay

@Composable
fun WorkoutSummaryScreen(
    viewModel: FitPulseViewModel,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val workout = viewModel.lastFinishedWorkout.collectAsState().value
    val xpGain = viewModel.lastXPGain.collectAsState().value
    if (workout == null) {
        onDismiss()
        return
    }

    val safeExercises = workout.exercises ?: emptyList()
    val totalSets = safeExercises.sumOf { (it.sets ?: emptyList()).size }
    val completedSets = safeExercises.sumOf { ex -> (ex.sets ?: emptyList()).count { it.isCompleted } }
    val totalReps = safeExercises.sumOf { ex -> (ex.sets ?: emptyList()).filter { it.isCompleted }.sumOf { it.reps } }
    val prCount = workout.personalRecordCount
    val summaryInsight = remember(workout) { buildWorkoutSummaryInsight(workout) }
    val prHighlights = remember(workout) { buildWorkoutPersonalRecordHighlights(workout) }
    val ratingLabel = remember(workout.rating) { workout.rating.takeIf { it > 0 }?.let { "$it/5 rating" } }
    val moodLabel = remember(workout.mood) {
        workout.mood?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
    }

    // Entrance animation
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
    }

    // Animated counter values
    val animatedDuration by animateIntAsState(
        targetValue = if (showContent) workout.durationMinutes else 0,
        animationSpec = tween(1200, easing = EaseOutCubic), label = "duration"
    )
    val animatedCalories by animateIntAsState(
        targetValue = if (showContent) workout.totalCalories else 0,
        animationSpec = tween(1400, easing = EaseOutCubic), label = "calories"
    )
    val animatedVolume by animateFloatAsState(
        targetValue = if (showContent) workout.totalVolume else 0f,
        animationSpec = tween(1600, easing = EaseOutCubic), label = "volume"
    )
    val animatedReps by animateIntAsState(
        targetValue = if (showContent) totalReps else 0,
        animationSpec = tween(1300, easing = EaseOutCubic), label = "reps"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        FitPulseTheme.colors.background,
                        GradientStart.copy(alpha = 0.1f),
                        FitPulseTheme.colors.background
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Celebration Header
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -50 }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Trophy icon with glow
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    Warning.copy(alpha = 0.3f),
                                    Warning.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (prCount > 0) "🏆" else "💪",
                        style = FitPulseTypography.displayLarge,
                        fontSize = 52.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (prCount > 0) "New Records!" else "Workout Complete!",
                    style = FitPulseTypography.displaySmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = workout.name,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )

                if (prCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Warning.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎉", style = FitPulseTypography.titleMedium)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "$prCount Personal Record${if (prCount > 1) "s" else ""} Broken!",
                                style = FitPulseTypography.labelLarge,
                                color = Warning,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Grid
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(tween(600, delayMillis = 300)) { 40 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.Timer,
                        value = Utils.formatDuration(animatedDuration),
                        label = "Duration",
                        color = Primary,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.LocalFireDepartment,
                        value = "$animatedCalories",
                        label = "Calories",
                        color = Accent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.FitnessCenter,
                        value = "${String.format("%.0f", animatedVolume)} kg",
                        label = "Volume",
                        color = Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.Repeat,
                        value = "$animatedReps",
                        label = "Total Reps",
                        color = Success,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.CheckCircle,
                        value = "$completedSets / $totalSets",
                        label = "Sets Done",
                        color = GradientCyan,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.AutoMirrored.Filled.ListAlt,
                        value = "${safeExercises.size}",
                        label = "Exercises",
                        color = Warning,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 450)) + slideInVertically(tween(600, delayMillis = 450)) { 40 }
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Primary.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = summaryInsight.title,
                            style = FitPulseTypography.titleLarge,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = summaryInsight.message,
                            style = FitPulseTypography.bodyMedium,
                            color = FitPulseTheme.colors.textSecondary
                        )
                    }
                }
            }
        }

        if (prHighlights.isNotEmpty() || ratingLabel != null || moodLabel != null || !workout.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
        }

        AnimatedVisibility(
            visible = showContent && (prHighlights.isNotEmpty() || ratingLabel != null || moodLabel != null || !workout.notes.isNullOrBlank()),
            enter = fadeIn(tween(600, delayMillis = 520)) + slideInVertically(tween(600, delayMillis = 520)) { 40 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (prHighlights.isNotEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PR Highlights",
                            style = FitPulseTypography.titleMedium,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        prHighlights.take(3).forEach { highlight ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Warning,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = highlight,
                                    style = FitPulseTypography.bodyMedium,
                                    color = FitPulseTheme.colors.textSecondary,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (highlight != prHighlights.take(3).last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                if (ratingLabel != null || moodLabel != null || !workout.notes.isNullOrBlank()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Session Review",
                            style = FitPulseTypography.titleMedium,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        if (ratingLabel != null || moodLabel != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ratingLabel?.let {
                                    FitPulseChip(text = it, selected = true, color = Warning)
                                }
                                moodLabel?.let {
                                    FitPulseChip(text = it, selected = true, color = Secondary)
                                }
                            }
                        }

                        if (!workout.notes.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = workout.notes.orEmpty(),
                                style = FitPulseTypography.bodyMedium,
                                color = FitPulseTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exercise Breakdown
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 600)) + slideInVertically(tween(600, delayMillis = 600)) { 40 }
        ) {
            Column {
                Text(
                    "Exercise Breakdown",
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                safeExercises.forEach { exercise ->
                    val safeSets = exercise.sets ?: emptyList()
                    val exerciseCompletedSets = safeSets.count { it.isCompleted }
                    val bestSet = safeSets.filter { it.isCompleted && it.weightKg > 0 }
                        .maxByOrNull { it.weightKg * it.reps }
                    val hasPR = safeSets.any { it.isPersonalRecord }

                    GlassCard(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        exercise.exerciseName,
                                        style = FitPulseTypography.titleMedium,
                                        color = FitPulseTheme.colors.textPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (hasPR) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🏆", style = FitPulseTypography.bodyMedium)
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FitPulseChip(
                                        text = "$exerciseCompletedSets/${safeSets.size} sets",
                                        selected = exerciseCompletedSets == safeSets.size,
                                        color = if (exerciseCompletedSets == safeSets.size) Success else Warning
                                    )
                                    if (bestSet != null) {
                                        FitPulseChip(
                                            text = "Best: ${bestSet.weightKg}kg × ${bestSet.reps}",
                                            selected = true,
                                            color = Primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // XP Gained Indicator
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 800)) + scaleIn(tween(600, delayMillis = 800))
        ) {
            GradientCard(
                modifier = Modifier.fillMaxWidth(),
                colors = listOf(Primary.copy(alpha = 0.4f), GradientEnd.copy(alpha = 0.3f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚡", style = FitPulseTypography.headlineLarge)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = xpGain?.let { "+${it.xpGained} XP earned" } ?: "+50 XP earned",
                            style = FitPulseTypography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Keep it up! Consistency is key 🔥",
                            style = FitPulseTypography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(600, delayMillis = 1000)) + slideInVertically(tween(600, delayMillis = 1000)) { 40 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GradientButton(
                    text = "Share Workout",
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Share,
                    colors = listOf(Primary, GradientEnd)
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FitPulseTheme.colors.card
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Done",
                        style = FitPulseTypography.titleMedium,
                        color = FitPulseTheme.colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun SummaryStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.8f)),
        border = CardDefaults.outlinedCardBorder().let {
            androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = FitPulseTypography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary
            )
        }
    }
}
