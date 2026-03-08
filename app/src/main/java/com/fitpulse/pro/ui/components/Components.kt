package com.fitpulse.pro.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.ui.theme.*

// ============================================================================
// GLASSMORPHISM CARD
// ============================================================================
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = FitPulseTheme.colors
    val shape = RoundedCornerShape(20.dp)
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = colors.card.copy(alpha = 0.7f)
        ),
        border = CardDefaults.outlinedCardBorder().let {
            androidx.compose.foundation.BorderStroke(1.dp, colors.border.copy(alpha = 0.3f))
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// ============================================================================
// GRADIENT CARD
// ============================================================================
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(GradientStart, GradientEnd),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = colors,
                        start = Offset.Zero,
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(16.dp),
            content = content
        )
    }
}

// ============================================================================
// STAT CARD (for dashboard)
// ============================================================================
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val colors = FitPulseTheme.colors
    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = FitPulseTypography.labelMedium,
                    color = colors.textTertiary
                )
                Text(
                    text = value,
                    style = FitPulseTypography.headlineMedium,
                    color = colors.textPrimary
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        style = FitPulseTypography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}

// ============================================================================
// CIRCULAR PROGRESS INDICATOR (Ring)
// ============================================================================
@Composable
fun CircularProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 10.dp,
    backgroundColor: Color = FitPulseTheme.colors.border.copy(alpha = 0.3f),
    progressColor: Color = MaterialTheme.colorScheme.primary,
    secondaryProgressColor: Color? = null,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        val effectiveProgressColor = secondaryProgressColor?.let {
            lerp(progressColor, it, 0.5f)
        } ?: progressColor

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val radius = (this.size.minDimension - stroke) / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Background ring
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                color = effectiveProgressColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        content()
    }
}

// ============================================================================
// SECTION HEADER
// ============================================================================
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    val colors = FitPulseTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = FitPulseTypography.headlineSmall,
            color = colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() }
        )
        if (actionText != null && onAction != null) {
            TextButton(
                onClick = onAction,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = actionText,
                    style = FitPulseTypography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ============================================================================
// GRADIENT BUTTON
// ============================================================================
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(GradientStart, GradientEnd),
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val themeColors = FitPulseTheme.colors
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (enabled) colors else listOf(themeColors.textTertiary, themeColors.textTertiary)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = themeColors.textOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = FitPulseTypography.labelLarge,
                    color = themeColors.textOnPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================================
// CHIP / TAG
// ============================================================================
@Composable
fun FitPulseChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null
) {
    val themeColors = FitPulseTheme.colors
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier
                        .minimumInteractiveComponentSize()
                        .clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) color.copy(alpha = 0.2f) else Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) color else themeColors.border
        )
    ) {
        Text(
            text = text,
            style = FitPulseTypography.labelMedium,
            color = if (selected) color else themeColors.textSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// ============================================================================
// EXERCISE LIST ITEM
// ============================================================================
@Composable
fun ExerciseListItem(
    exerciseName: String,
    category: String,
    muscleGroup: String,
    difficulty: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = FitPulseTheme.colors
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseName,
                    style = FitPulseTypography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$muscleGroup - $category",
                    style = FitPulseTypography.bodyMedium,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FitPulseChip(
                text = difficulty,
                color = when (difficulty) {
                    "BEGINNER" -> Success
                    "INTERMEDIATE" -> Warning
                    "ADVANCED" -> Accent
                    else -> Error
                },
                selected = true
            )
        }
    }
}

// ============================================================================
// ANIMATED COUNTER
// ============================================================================
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = FitPulseTypography.statValue,
    color: Color = FitPulseTheme.colors.textPrimary
) {
    val animatedCount by animateIntAsState(
        targetValue = count,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "counter"
    )

    Text(
        text = animatedCount.toString(),
        style = style,
        color = color,
        modifier = modifier
    )
}

// ============================================================================
// EMPTY STATE
// ============================================================================
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    val colors = FitPulseTheme.colors
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = FitPulseTypography.headlineSmall,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = FitPulseTypography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            GradientButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================================
// MACRO BAR
// ============================================================================
@Composable
fun MacroBar(
    label: String,
    current: Float,
    goal: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val themeColors = FitPulseTheme.colors
    val progress = if (goal > 0f) (current / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "macro"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = FitPulseTypography.labelMedium, color = themeColors.textSecondary)
            Text(
                text = "${current.toInt()}g / ${goal.toInt()}g",
                style = FitPulseTypography.labelMedium,
                color = themeColors.textPrimary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(themeColors.border.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(color, color.copy(alpha = 0.7f))
                        )
                    )
            )
        }
    }
}

// ============================================================================
// WORKOUT SUMMARY CARD
// ============================================================================
@Composable
fun WorkoutSummaryCard(
    name: String,
    date: String,
    duration: String,
    exercises: Int,
    volume: String,
    calories: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = FitPulseTheme.colors
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(GradientStart.copy(alpha = 0.3f), GradientEnd.copy(alpha = 0.3f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = FitPulseTypography.titleLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "$date - $duration",
                    style = FitPulseTypography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$exercises exercise${if (exercises == 1) "" else "s"}",
                    style = FitPulseTypography.labelSmall,
                    color = colors.textTertiary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$calories cal",
                    style = FitPulseTypography.labelLarge,
                    color = Accent
                )
                Text(
                    text = volume,
                    style = FitPulseTypography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

// ============================================================================
// ACHIEVEMENT BADGE
// ============================================================================
@Composable
fun AchievementBadge(
    name: String,
    description: String,
    isUnlocked: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val colors = FitPulseTheme.colors
    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressRing(
                progress = progress,
                size = 64.dp,
                strokeWidth = 6.dp,
                progressColor = if (isUnlocked) Success else MaterialTheme.colorScheme.primary,
                backgroundColor = colors.border.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) Success else colors.textTertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = name,
                style = FitPulseTypography.labelLarge,
                color = if (isUnlocked) colors.textPrimary else colors.textTertiary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = FitPulseTypography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
