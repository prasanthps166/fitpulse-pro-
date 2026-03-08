package com.fitpulse.pro.ui.screens.mindfulness

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.ui.components.CircularProgressRing
import com.fitpulse.pro.ui.components.GlassCard
import com.fitpulse.pro.ui.components.GradientButton
import com.fitpulse.pro.ui.theme.*
import kotlinx.coroutines.delay

// Meditation session types
data class MeditationSession(
    val name: String,
    val emoji: String,
    val subtitle: String,
    val defaultMinutes: Int,
    val color: Color,
    val guidanceText: List<String> // Guidance prompts shown during session
)

val meditationSessions = listOf(
    MeditationSession(
        "Focus", "🎯", "Sharpen your mind",
        10, Primary,
        listOf("Focus on your breath", "Clear your thoughts", "Stay present", "Notice without judging")
    ),
    MeditationSession(
        "Sleep", "🌙", "Wind down & relax",
        15, GradientCyan,
        listOf("Let go of the day", "Relax your body", "Drift peacefully", "Release all tension")
    ),
    MeditationSession(
        "Stress", "🧘", "Release & let go",
        10, Success,
        listOf("Breathe deeply", "Release tension", "Let worries fade", "Find your calm center")
    ),
    MeditationSession(
        "Body Scan", "🫧", "Full body awareness",
        15, Secondary,
        listOf("Notice your toes", "Relax your legs", "Soften your belly", "Release your shoulders")
    ),
    MeditationSession(
        "Morning", "☀️", "Start fresh",
        5, Warning,
        listOf("Set your intention", "Embrace the day", "Feel grateful", "Carry this peace with you")
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationScreen(
    onBack: () -> Unit
) {
    var isTimerRunning by remember { mutableStateOf(false) }
    var selectedSessionIndex by remember { mutableIntStateOf(0) }
    var selectedDurationMinutes by remember { mutableIntStateOf(meditationSessions[0].defaultMinutes) }
    var timeRemainingSeconds by remember { mutableLongStateOf(meditationSessions[0].defaultMinutes * 60L) }
    var currentGuidanceIndex by remember { mutableIntStateOf(0) }

    val selectedSession = meditationSessions[selectedSessionIndex]

    // When session/duration changes, reset
    LaunchedEffect(selectedSessionIndex) {
        if (!isTimerRunning) {
            selectedDurationMinutes = selectedSession.defaultMinutes
            timeRemainingSeconds = selectedSession.defaultMinutes * 60L
        }
    }
    LaunchedEffect(selectedDurationMinutes) {
        if (!isTimerRunning) {
            timeRemainingSeconds = selectedDurationMinutes * 60L
        }
    }

    // Timer logic
    LaunchedEffect(isTimerRunning, timeRemainingSeconds) {
        if (isTimerRunning && timeRemainingSeconds > 0) {
            delay(1000L)
            timeRemainingSeconds--
        } else if (timeRemainingSeconds == 0L && isTimerRunning) {
            isTimerRunning = false
        }
    }

    // Rotate guidance text every 15 seconds
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            currentGuidanceIndex = 0
            while (isTimerRunning) {
                delay(15000L)
                currentGuidanceIndex = (currentGuidanceIndex + 1) % selectedSession.guidanceText.size
            }
        }
    }

    val progress = 1f - (timeRemainingSeconds.toFloat() / (selectedDurationMinutes * 60f))

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
            Text("Meditation", style = FitPulseTypography.headlineMedium, color = FitPulseTheme.colors.textPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Session Type Selector
        if (!isTimerRunning) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(meditationSessions.size) { index ->
                    val session = meditationSessions[index]
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedSessionIndex == index)
                                session.color.copy(alpha = 0.15f) else FitPulseTheme.colors.surface
                        ),
                        border = if (selectedSessionIndex == index)
                            CardDefaults.outlinedCardBorder().let {
                                androidx.compose.foundation.BorderStroke(1.5.dp, session.color.copy(alpha = 0.5f))
                            } else null,
                        onClick = { selectedSessionIndex = index },
                        modifier = Modifier.width(100.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(session.emoji, style = FitPulseTypography.headlineSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                session.name,
                                style = FitPulseTypography.labelMedium,
                                color = if (selectedSessionIndex == index) session.color
                                    else FitPulseTheme.colors.textSecondary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "${session.defaultMinutes}m",
                                style = FitPulseTypography.labelSmall,
                                color = FitPulseTheme.colors.textTertiary
                            )
                        }
                    }
                }
            }
        } else {
            // Active session indicator
            Text(
                "${selectedSession.emoji} ${selectedSession.name}",
                style = FitPulseTypography.titleLarge,
                color = selectedSession.color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Timer Display
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(260.dp)
        ) {
            // Background ring
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = FitPulseTheme.colors.surface,
                strokeWidth = 12.dp
            )

            // Progress ring
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = selectedSession.color,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val minutes = timeRemainingSeconds / 60
                val seconds = timeRemainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = FitPulseTypography.displayLarge,
                    color = FitPulseTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isTimerRunning) selectedSession.guidanceText.getOrElse(currentGuidanceIndex) { "Breathe" }
                        else "Ready to begin",
                    style = FitPulseTypography.bodyMedium,
                    color = if (isTimerRunning) selectedSession.color else FitPulseTheme.colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Duration Selection
        if (!isTimerRunning) {
            Text("Duration", style = FitPulseTypography.titleMedium, color = FitPulseTheme.colors.textSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(3, 5, 10, 15, 20).forEach { mins ->
                    FilterChip(
                        selected = selectedDurationMinutes == mins,
                        onClick = { selectedDurationMinutes = mins },
                        label = { Text("$mins min") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = selectedSession.color.copy(alpha = 0.2f),
                            selectedLabelColor = selectedSession.color,
                            containerColor = FitPulseTheme.colors.surface,
                            labelColor = FitPulseTheme.colors.textSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    isTimerRunning = !isTimerRunning
                    if (timeRemainingSeconds == 0L) {
                        timeRemainingSeconds = selectedDurationMinutes * 60L
                        isTimerRunning = true
                    }
                },
                modifier = Modifier
                    .height(60.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) Error else selectedSession.color
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Icon(
                    imageVector = if (isTimerRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isTimerRunning) "Stop" else "Start",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isTimerRunning) "Stop" else "Start",
                    style = FitPulseTypography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}


