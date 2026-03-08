package com.fitpulse.pro.ui.screens.workout

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.components.GlassCard
import com.fitpulse.pro.ui.components.GradientButton
import com.fitpulse.pro.ui.components.GradientCard
import com.fitpulse.pro.ui.components.FitPulseChip
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel

@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    viewModel: FitPulseViewModel,
    onBack: () -> Unit,
    onRepeatWorkout: (Long) -> Unit
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    
    LaunchedEffect(workoutId) {
        try {
            workout = viewModel.getWorkoutById(workoutId)
        } catch (e: Exception) {
            workout = null
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 20.dp, top = 48.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FitPulseTheme.colors.textPrimary)
            }
            Text(
                text = "Workout Detail",
                style = FitPulseTypography.headlineMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
        
        if (workout == null) {
            // Loading / not found
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading workout...",
                        style = FitPulseTypography.bodyMedium,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
            }
        } else {
            val w = workout!!
            
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Workout Name & Date Header
                item {
                    GradientCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = listOf(GradientStart.copy(alpha = 0.35f), GradientEnd.copy(alpha = 0.25f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = w.name,
                                    style = FitPulseTypography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = try {
                                        Utils.formatDate(w.createdAt) + " • " + Utils.formatTime(w.startTime)
                                    } catch (e: Exception) {
                                        Utils.formatDate(w.createdAt)
                                    },
                                    style = FitPulseTypography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        // Mood & Rating
                        if (w.rating > 0 || w.mood != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                w.mood?.let { mood ->
                                    val emoji = when (mood) {
                                        WorkoutMood.GREAT -> "🔥"
                                        WorkoutMood.GOOD -> "💪"
                                        WorkoutMood.OKAY -> "👍"
                                        WorkoutMood.TIRED -> "😮‍💨"
                                        WorkoutMood.TERRIBLE -> "😵"
                                    }
                                    val label = mood.name.lowercase().replaceFirstChar { it.uppercase() }
                                    Surface(
                                        color = Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            Text(emoji, style = FitPulseTypography.bodyMedium)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(label, style = FitPulseTypography.bodyMedium, color = Color.White)
                                        }
                                    }
                                }
                                if (w.rating > 0) {
                                    Surface(
                                        color = Color.White.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                            (1..5).forEach { star ->
                                                Icon(
                                                    if (star <= w.rating) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    tint = if (star <= w.rating) Warning else Color.White.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Stats Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailStatCard(
                            title = "Duration",
                            value = Utils.formatDuration(w.durationMinutes),
                            icon = "⏱️",
                            color = Primary,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatCard(
                            title = "Calories",
                            value = "${w.totalCalories}",
                            icon = "🔥",
                            color = Accent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailStatCard(
                            title = "Total Volume",
                            value = Utils.formatWeight(w.totalVolume),
                            icon = "🏋️",
                            color = Secondary,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatCard(
                            title = "Exercises",
                            value = "${(w.exercises ?: emptyList()).size}",
                            icon = "💪",
                            color = Success,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Additional computed stats
                item {
                    val safeExercises = w.exercises ?: emptyList()
                    val totalSets = safeExercises.sumOf { (it.sets ?: emptyList()).size }
                    val completedSets = safeExercises.sumOf { ex -> (ex.sets ?: emptyList()).count { it.isCompleted } }
                    val totalReps = safeExercises.sumOf { ex -> (ex.sets ?: emptyList()).filter { it.isCompleted }.sumOf { it.reps } }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DetailStatCard(
                            title = "Sets Done",
                            value = "$completedSets / $totalSets",
                            icon = "✅",
                            color = Success,
                            modifier = Modifier.weight(1f)
                        )
                        DetailStatCard(
                            title = "Total Reps",
                            value = "$totalReps",
                            icon = "🔄",
                            color = Warning,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // Notes
                if (w.notes?.isNotBlank() == true) {
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Notes,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Notes",
                                    style = FitPulseTypography.titleMedium,
                                    color = FitPulseTheme.colors.textPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = w.notes.orEmpty(),
                                style = FitPulseTypography.bodyMedium,
                                color = FitPulseTheme.colors.textSecondary
                            )
                        }
                    }
                }
                
                // Exercises Section Header
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ListAlt,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Exercise Breakdown",
                            style = FitPulseTypography.headlineSmall,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Exercise Detail Cards
                items(w.exercises ?: emptyList()) { exercise ->
                    ExerciseDetailCard(exercise = exercise)
                }
                
                // Repeat Workout Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    GradientButton(
                        text = "Repeat This Workout",
                        onClick = { onRepeatWorkout(w.id) },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Replay,
                        colors = listOf(Secondary.copy(alpha = 0.8f), GradientCyan.copy(alpha = 0.8f))
                    )
                }
                
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f)),
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
            Text(icon, style = FitPulseTypography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = FitPulseTypography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ExerciseDetailCard(exercise: WorkoutExercise) {
    val safeSets = exercise.sets ?: emptyList()
    val completedSets = safeSets.count { it.isCompleted }
    val totalVolume = safeSets
        .filter { it.isCompleted }
        .sumOf { (it.weightKg * it.reps).toDouble() }
        .toFloat()
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // Exercise Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Primary.copy(alpha = 0.3f), GradientEnd.copy(alpha = 0.2f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.exerciseName,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitPulseChip(
                        text = "$completedSets / ${safeSets.size} sets",
                        selected = true,
                        color = if (completedSets == safeSets.size) Success else Warning
                    )
                    if (totalVolume > 0) {
                        FitPulseChip(
                            text = Utils.formatWeight(totalVolume),
                            selected = true,
                            color = Secondary
                        )
                    }
                }
            }
        }
        
        if (safeSets.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            
            // Set Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SET", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.width(40.dp))
                Text("WEIGHT", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("REPS", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("STATUS", style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary, modifier = Modifier.width(60.dp), textAlign = TextAlign.End)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Border.copy(alpha = 0.2f))
            
            // Set Rows
            safeSets.forEach { set ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Set number with type indicators
                    Row(modifier = Modifier.width(40.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (set.isWarmup) "W" else "${set.setNumber}",
                            style = FitPulseTypography.titleMedium,
                            color = when {
                                set.isCompleted -> Success
                                set.isWarmup -> Warning
                                else -> FitPulseTheme.colors.textTertiary
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Weight
                    Text(
                        text = if (set.weightKg > 0) "${set.weightKg} kg" else "—",
                        style = FitPulseTypography.bodyMedium,
                        color = FitPulseTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    // Reps
                    Text(
                        text = if (set.reps > 0) "${set.reps}" else "—",
                        style = FitPulseTypography.bodyMedium,
                        color = FitPulseTheme.colors.textPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    
                    // Status
                    Row(
                        modifier = Modifier.width(60.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (set.isDropSet) {
                            Text(
                                text = "DROP",
                                style = FitPulseTypography.labelSmall,
                                color = Accent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        if (set.isFailure) {
                            Text(
                                text = "F",
                                style = FitPulseTypography.labelSmall,
                                color = Error,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Icon(
                            if (set.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (set.isCompleted) Success else FitPulseTheme.colors.textTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            // RPE if present
            val setsWithRpe = safeSets.filter { it.rpe != null && it.rpe > 0f }
            if (setsWithRpe.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Border.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                val avgRpe = setsWithRpe.map { it.rpe!! }.average()
                Text(
                    text = "Avg RPE: ${String.format("%.1f", avgRpe)} / 10",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
        }
        
        // Notes for exercise
        if (exercise.notes?.isNotBlank() == true) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Border.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "📝 ${exercise.notes}",
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textSecondary
            )
        }
    }
}

