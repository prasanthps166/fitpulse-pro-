package com.fitpulse.pro.ui.screens.learn

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.viewmodel.FitPulseViewModel

@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    viewModel: FitPulseViewModel,
    onBack: () -> Unit
) {
    val exercises by viewModel.allExercises.collectAsState()
    val exercise = exercises.find { it.id == exerciseId }

    if (exercise == null) {
        EmptyState(
            icon = Icons.Default.Error,
            title = "Exercise Not Found",
            subtitle = "This exercise could not be loaded",
            actionText = "Go Back",
            onAction = onBack
        )
        return
    }

    val difficultyColor = when (exercise.difficulty) {
        Difficulty.BEGINNER -> Success
        Difficulty.INTERMEDIATE -> Warning
        Difficulty.ADVANCED -> Accent
        Difficulty.EXPERT -> Error
    }

    val categoryIcon = when (exercise.category) {
        ExerciseCategory.STRENGTH -> Icons.Default.FitnessCenter
        ExerciseCategory.CARDIO -> Icons.AutoMirrored.Filled.DirectionsRun
        ExerciseCategory.FLEXIBILITY -> Icons.Default.SelfImprovement
        ExerciseCategory.BALANCE -> Icons.Default.Balance
        ExerciseCategory.PLYOMETRICS -> Icons.Default.FlashOn
        ExerciseCategory.CALISTHENICS -> Icons.Default.AccessibilityNew
        ExerciseCategory.OLYMPIC_LIFTING -> Icons.Default.EmojiEvents
        ExerciseCategory.YOGA -> Icons.Default.SelfImprovement
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 20.dp, top = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FitPulseTheme.colors.textPrimary)
            }
            Text("Exercise Detail", style = FitPulseTypography.headlineMedium, color = FitPulseTheme.colors.textPrimary)
        }

        // Hero section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        listOf(Primary.copy(alpha = 0.25f), Accent.copy(alpha = 0.15f))
                    )
                )
                .border(1.dp, Border.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        categoryIcon,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    exercise.name,
                    style = FitPulseTypography.displaySmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    exercise.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = FitPulseTypography.bodyLarge,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
        }

        // Quick info row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoChip(
                label = "Difficulty",
                value = exercise.difficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                color = difficultyColor,
                modifier = Modifier.weight(1f)
            )
            InfoChip(
                label = "Equipment",
                value = exercise.equipment.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                color = Secondary,
                modifier = Modifier.weight(1f)
            )
            InfoChip(
                label = "Cal/min",
                value = "${exercise.caloriesPerMinute.toInt()}",
                color = Warning,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Primary muscle group
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                "Primary Muscle",
                style = FitPulseTypography.headlineSmall,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FitPulseChip(
                text = exercise.muscleGroup.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                selected = true,
                color = Primary
            )
        }

        // Secondary muscles
        if (exercise.secondaryMuscles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Secondary Muscles",
                    style = FitPulseTypography.headlineSmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(exercise.secondaryMuscles) { muscle ->
                        FitPulseChip(
                            text = muscle.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                            selected = true,
                            color = Secondary
                        )
                    }
                }
            }
        }

        // Description
        if (exercise.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Description",
                    style = FitPulseTypography.headlineSmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f))
                ) {
                    Text(
                        exercise.description,
                        style = FitPulseTypography.bodyLarge,
                        color = FitPulseTheme.colors.textSecondary,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = FitPulseTypography.bodyLarge.lineHeight
                    )
                }
            }
        }

        // Instructions
        if (exercise.instructions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Instructions",
                    style = FitPulseTypography.headlineSmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        exercise.instructions.forEachIndexed { index, instruction ->
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        style = FitPulseTypography.labelLarge,
                                        color = Primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    instruction,
                                    style = FitPulseTypography.bodyMedium,
                                    color = FitPulseTheme.colors.textPrimary,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tips
        if (exercise.tips.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Pro Tips 💡",
                    style = FitPulseTypography.headlineSmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                exercise.tips.forEach { tip ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.08f)),
                        border = CardDefaults.outlinedCardBorder().let {
                            BorderStroke(1.dp, Warning.copy(alpha = 0.2f))
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Warning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                tip,
                                style = FitPulseTypography.bodyMedium,
                                color = FitPulseTheme.colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = CardDefaults.outlinedCardBorder().let {
            BorderStroke(1.dp, color.copy(alpha = 0.2f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = FitPulseTypography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

