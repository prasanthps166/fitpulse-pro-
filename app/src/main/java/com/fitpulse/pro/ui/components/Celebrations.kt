package com.fitpulse.pro.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.ui.theme.*
import kotlin.math.sin
import kotlin.random.Random

/**
 * Confetti celebration overlay animation.
 * Shows when the user hits a PR, levels up, or completes an achievement.
 */
data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speedX: Float,
    val speedY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ConfettiOverlay(
    isActive: Boolean,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val confettiColors = listOf(
        Warning, Primary, Secondary, Accent, Success, GradientCyan,
        ChartPurple, ChartOrange, ChartPink, ChartYellow
    )

    // Generate particles
    val particles = remember {
        (0..80).map {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f - 0.1f, // start above screen
                speedX = (Random.nextFloat() - 0.5f) * 0.01f,
                speedY = Random.nextFloat() * 0.006f + 0.002f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
                color = confettiColors.random(),
                size = Random.nextFloat() * 10f + 4f
            )
        }
    }

    val animProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(3000, easing = LinearEasing),
        finishedListener = { onComplete() },
        label = "confetti"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val t = animProgress
        particles.forEach { p ->
            val currentX = (p.x + p.speedX * t * 400f) * size.width
            val currentY = (p.y + p.speedY * t * 400f) * size.height
            val wobble = sin(t * 10f + p.rotation) * 20f

            if (currentY in -20f..size.height + 20f) {
                drawCircle(
                    color = p.color.copy(alpha = (1f - t * 0.7f).coerceAtLeast(0f)),
                    radius = p.size * (1f - t * 0.3f),
                    center = Offset(currentX + wobble, currentY)
                )
            }
        }
    }
}

/**
 * Level-up celebration with ring burst effect.
 */
@Composable
fun LevelUpCelebration(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    var showConfetti by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize()) {
        // Confetti background
        ConfettiOverlay(
            isActive = showConfetti,
            onComplete = { showConfetti = false }
        )

        // Level-up card would overlay here
        // This is handled by the caller composable
    }
}
