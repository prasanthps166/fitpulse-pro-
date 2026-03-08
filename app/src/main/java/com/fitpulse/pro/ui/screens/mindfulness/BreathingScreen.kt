package com.fitpulse.pro.ui.screens.mindfulness

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.ui.theme.*
import kotlinx.coroutines.delay

// Breathing patterns: name, inhale seconds, hold seconds, exhale seconds, hold-after-exhale seconds
data class BreathingPattern(
    val name: String,
    val subtitle: String,
    val emoji: String,
    val inhale: Int,
    val holdIn: Int,
    val exhale: Int,
    val holdOut: Int = 0,
    val color: Color
)

val breathingPatterns = listOf(
    BreathingPattern("4-7-8 Relaxing", "Promotes relaxation & sleep", "🌙", 4, 7, 8, 0, GradientCyan),
    BreathingPattern("Box Breathing", "Military focus technique", "🎯", 4, 4, 4, 4, Primary),
    BreathingPattern("Wim Hof", "Energizing power breath", "❄️", 2, 0, 2, 0, Accent),
    BreathingPattern("Calm Breath", "Simple stress relief", "🍃", 4, 2, 6, 0, Success)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    onBack: () -> Unit
) {
    var isActive by remember { mutableStateOf(false) }
    var phase by remember { mutableStateOf("Ready") }
    var instruction by remember { mutableStateOf("Tap circle to start") }
    var selectedPatternIndex by remember { mutableIntStateOf(0) }
    var cycleCount by remember { mutableIntStateOf(0) }

    val selectedPattern = breathingPatterns[selectedPatternIndex]

    // Animation state
    val scaleAnim = remember { Animatable(1f) }

    LaunchedEffect(isActive, selectedPatternIndex) {
        if (isActive) {
            cycleCount = 0
            while (isActive) {
                // Inhale
                phase = "Inhale"
                instruction = "Breathe in..."
                scaleAnim.animateTo(
                    targetValue = 1.5f,
                    animationSpec = tween(selectedPattern.inhale * 1000, easing = LinearEasing)
                )

                // Hold after inhale
                if (selectedPattern.holdIn > 0) {
                    phase = "Hold"
                    instruction = "Hold..."
                    scaleAnim.animateTo(
                        targetValue = 1.52f,
                        animationSpec = keyframes {
                            durationMillis = selectedPattern.holdIn * 1000
                            1.5f at 0
                            1.52f at (selectedPattern.holdIn * 500)
                            1.5f at (selectedPattern.holdIn * 1000)
                        }
                    )
                }

                // Exhale
                phase = "Exhale"
                instruction = "Breathe out..."
                scaleAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(selectedPattern.exhale * 1000, easing = LinearEasing)
                )

                // Hold after exhale (Box Breathing)
                if (selectedPattern.holdOut > 0) {
                    phase = "Hold"
                    instruction = "Hold empty..."
                    delay(selectedPattern.holdOut * 1000L)
                }

                cycleCount++
            }
        } else {
            phase = "Ready"
            instruction = "Tap circle to start"
            scaleAnim.animateTo(1f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FitPulseTheme.colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Breathing", style = FitPulseTypography.headlineMedium, color = FitPulseTheme.colors.textPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pattern Selector
        if (!isActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                breathingPatterns.forEachIndexed { index, pattern ->
                    FilterChip(
                        selected = selectedPatternIndex == index,
                        onClick = { selectedPatternIndex = index },
                        label = {
                            Text(
                                "${pattern.emoji} ${pattern.name.split(" ").first()}",
                                style = FitPulseTypography.labelSmall,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = pattern.color.copy(alpha = 0.2f),
                            selectedLabelColor = pattern.color,
                            containerColor = FitPulseTheme.colors.surface,
                            labelColor = FitPulseTheme.colors.textSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedPatternIndex == index,
                            selectedBorderColor = pattern.color.copy(alpha = 0.5f),
                            borderColor = Border.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pattern info
            Text(
                selectedPattern.name,
                style = FitPulseTypography.titleMedium,
                color = FitPulseTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                selectedPattern.subtitle,
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Timing display
            val timingText = buildString {
                append("In: ${selectedPattern.inhale}s")
                if (selectedPattern.holdIn > 0) append(" → Hold: ${selectedPattern.holdIn}s")
                append(" → Out: ${selectedPattern.exhale}s")
                if (selectedPattern.holdOut > 0) append(" → Hold: ${selectedPattern.holdOut}s")
            }
            Text(
                timingText,
                style = FitPulseTypography.labelMedium,
                color = selectedPattern.color,
                textAlign = TextAlign.Center
            )
        } else {
            // Active — show cycle count
            Text(
                "Cycle $cycleCount",
                style = FitPulseTypography.titleMedium,
                color = FitPulseTheme.colors.textSecondary
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Outer guide circle
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(FitPulseTheme.colors.surface.copy(alpha = 0.5f), CircleShape)
            )

            // Breathing Circle
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value
                    )
                    .background(
                        color = when (phase) {
                            "Inhale" -> selectedPattern.color
                            "Hold" -> selectedPattern.color.copy(alpha = 0.7f)
                            "Exhale" -> selectedPattern.color.copy(alpha = 0.5f)
                            else -> TextSecondary
                        },
                        shape = CircleShape
                    )
                    .clickable { isActive = !isActive },
                contentAlignment = Alignment.Center
            ) {
                if (!isActive) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // Instructions
        Text(
            text = phase,
            style = FitPulseTypography.displayLarge,
            color = if (isActive) selectedPattern.color else FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = instruction,
            style = FitPulseTypography.headlineSmall,
            color = FitPulseTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stop button when active
        if (isActive) {
            Button(
                onClick = { isActive = false },
                colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Stop", style = FitPulseTypography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

