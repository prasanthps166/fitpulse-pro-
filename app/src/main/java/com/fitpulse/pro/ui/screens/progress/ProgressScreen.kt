package com.fitpulse.pro.ui.screens.progress

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: FitPulseViewModel,
    onNavigateToMeasurementLog: () -> Unit,
    onNavigateToArticle: (String) -> Unit
) {
    val latestMeasurement by viewModel.latestMeasurement.collectAsState()
    val recentMeasurements by viewModel.recentMeasurements.collectAsState()
    val allMeasurements by viewModel.allMeasurements.collectAsState()
    val weeklyStats by viewModel.weeklyStats.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()
    val totalWorkouts by viewModel.totalWorkoutCount.collectAsState()
    val totalVolume by viewModel.totalVolume.collectAsState()
    val maxStreak by viewModel.maxStreak.collectAsState()
    val calendarData by viewModel.calendarWorkoutData.collectAsState()

    var monthOffset by remember { mutableIntStateOf(0) }
    val statusSummary = remember(weeklyStats, totalWorkouts, maxStreak, allMeasurements) {
        buildProgressStatusSummary(
            weeklyStats = weeklyStats,
            totalWorkouts = totalWorkouts,
            maxStreak = maxStreak,
            measurements = allMeasurements
        )
    }
    val summaryMetrics = remember(weeklyStats) {
        buildProgressSummaryMetrics(weeklyStats)
    }
    val insights = remember(weeklyStats, totalWorkouts, maxStreak, allMeasurements, personalRecords) {
        buildProgressInsights(
            weeklyStats = weeklyStats,
            totalWorkouts = totalWorkouts,
            maxStreak = maxStreak,
            measurements = allMeasurements,
            personalRecords = personalRecords
        )
    }

    // Load calendar data on first composition and when month changes
    LaunchedEffect(monthOffset) {
        viewModel.loadCalendarData(monthOffset)
    }
    val colors = FitPulseTheme.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .testTag(TestTags.ProgressScreen),
        contentPadding = PaddingValues(
            horizontal = FitPulseLayout.ScreenHorizontalPadding,
            vertical = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ====== Header ======
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress",
                        style = FitPulseTypography.displayMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Track your fitness journey",
                        style = FitPulseTypography.bodyMedium,
                        color = colors.textSecondary
                    )
                }

                IconButton(
                    onClick = onNavigateToMeasurementLog,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Log measurement",
                        tint = Primary
                    )
                }
            }
        }

        // ====== Overview Stats ======
        item {
            ProgressOverviewSection(
                totalWorkouts = totalWorkouts,
                totalVolume = totalVolume ?: 0f,
                maxStreak = maxStreak ?: 0
            )
        }

        item {
            ProgressStatusCard(
                status = statusSummary,
                metrics = summaryMetrics,
                colors = colors
            )
        }

        item {
            ProgressInsightsCard(
                insights = insights,
                colors = colors,
                onNavigateToArticle = onNavigateToArticle
            )
        }

        // ====== Workout Calendar Heatmap ======
        item {
            WorkoutCalendarHeatmap(
                calendarData = calendarData,
                monthOffset = monthOffset,
                onPreviousMonth = { monthOffset-- },
                onNextMonth = { if (monthOffset < 0) monthOffset++ },
                colors = colors
            )
        }

        // ====== Weekly Activity Chart ======
        item {
            WeeklyActivityChart(
                weeklyStats = weeklyStats,
                colors = colors
            )
        }

        // ====== Weight Trend Chart ======
        item {
            WeightTrendChart(
                recentMeasurements = recentMeasurements,
                allMeasurements = allMeasurements,
                latestMeasurement = latestMeasurement,
                colors = colors
            )
        }

        // ====== Personal Records ======
        if (personalRecords.isNotEmpty()) {
            item {
                SectionHeader(title = "Personal Records")
            }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(items = personalRecords, key = { it.id }) { pr ->
                        PRCard(pr = pr, colors = colors)
                    }
                }
            }
        }

        // ====== Body Measurements ======
        item {
            MeasurementOverview(
                latestMeasurement = latestMeasurement,
                measurements = allMeasurements,
                recentMeasurements = recentMeasurements,
                onDeleteMeasurement = viewModel::deleteMeasurement,
                colors = colors
            )
        }

        // Bottom spacer
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ============================================================================
// WORKOUT CALENDAR HEATMAP
// ============================================================================
@Composable
fun WorkoutCalendarHeatmap(
    calendarData: Map<String, Int>,
    monthOffset: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    colors: FitPulseColors
) {
    val cal = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }

    val monthName = remember(monthOffset) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

    val daysInMonth = remember(monthOffset) {
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val firstDayOfWeek = remember(monthOffset) {
        val c = Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        (c.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Convert to Mon=0
    }
    val yearMonth = remember(monthOffset) {
        val c = Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }
        SimpleDateFormat("yyyy-MM", Locale.US).format(c.time)
    }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = colors.textSecondary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Workout Calendar",
                    style = FitPulseTypography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = monthName,
                    style = FitPulseTypography.bodySmall,
                    color = colors.textSecondary
                )
            }

            IconButton(
                onClick = onNextMonth,
                enabled = monthOffset < 0
            ) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = if (monthOffset < 0) colors.textSecondary else colors.textTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    style = FitPulseTypography.labelSmall,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDayOfWeek + 1

                    if (day in 1..daysInMonth) {
                        val dateStr = "$yearMonth-${String.format("%02d", day)}"
                        val workoutCount = calendarData[dateStr] ?: 0
                        val isToday = dateStr == today

                        CalendarDayCell(
                            day = day,
                            workoutCount = workoutCount,
                            isToday = isToday,
                            colors = colors,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                style = FitPulseTypography.labelSmall,
                color = colors.textTertiary
            )
            Spacer(modifier = Modifier.width(6.dp))
            listOf(0, 1, 2, 3).forEach { level ->
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(getHeatmapColor(level))
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = "More",
                style = FitPulseTypography.labelSmall,
                color = colors.textTertiary
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    day: Int,
    workoutCount: Int,
    isToday: Boolean,
    colors: FitPulseColors,
    modifier: Modifier = Modifier
) {
    val bgColor = getHeatmapColor(workoutCount)
    val borderColor = if (isToday) Primary else Color.Transparent

    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .then(
                if (isToday) Modifier.border(
                    1.5.dp,
                    borderColor,
                    RoundedCornerShape(6.dp)
                ) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$day",
            style = FitPulseTypography.labelSmall,
            fontSize = 10.sp,
            color = if (workoutCount > 0) Color.White else colors.textTertiary,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun getHeatmapColor(workoutCount: Int): Color {
    return when {
        workoutCount == 0 -> Color(0xFF1A1A2E)
        workoutCount == 1 -> Color(0xFF2D4A3E)
        workoutCount == 2 -> Color(0xFF1B7A42)
        else -> Color(0xFF00E676)
    }
}

// ============================================================================
// WEEKLY ACTIVITY CHART
// ============================================================================
@Composable
fun WeeklyActivityChart(
    weeklyStats: List<DailyStats>,
    colors: FitPulseColors
) {
    var selectedChartType by remember { mutableIntStateOf(0) }
    val chartTypes = listOf("Minutes", "Calories", "Volume")

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Activity",
                style = FitPulseTypography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chart type chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chartTypes.forEachIndexed { index, label ->
                FitPulseChip(
                    text = label,
                    selected = selectedChartType == index,
                    onClick = { selectedChartType = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bar chart
        val displayStats = if (weeklyStats.size >= 7) weeklyStats.takeLast(7)
        else weeklyStats

        if (displayStats.isNotEmpty()) {
            val values = displayStats.map { stat ->
                when (selectedChartType) {
                    0 -> stat.activeMinutes.toFloat()
                    1 -> stat.caloriesBurned.toFloat()
                    else -> stat.totalVolume
                }
            }

            val maxVal = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
            val avgVal = if (values.isNotEmpty()) values.average().toFloat() else 0f
            val totalVal = values.sum()

            // Summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (selectedChartType) {
                            0 -> "${totalVal.toInt()} min"
                            1 -> "${totalVal.toInt()} kcal"
                            else -> "${Utils.formatWeight(totalVal)}"
                        },
                        style = FitPulseTypography.titleSmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total", style = FitPulseTypography.labelSmall, color = colors.textTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (selectedChartType) {
                            0 -> "${avgVal.toInt()} min"
                            1 -> "${avgVal.toInt()} kcal"
                            else -> "${Utils.formatWeight(avgVal)}"
                        },
                        style = FitPulseTypography.titleSmall,
                        color = ChartCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Average", style = FitPulseTypography.labelSmall, color = colors.textTertiary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                displayStats.forEachIndexed { index, _ ->
                    val value = values.getOrElse(index) { 0f }
                    val heightFraction = if (maxVal > 0) (value / maxVal) else 0f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Value label
                        Text(
                            text = if (value > 0) "${value.toInt()}" else "",
                            style = FitPulseTypography.labelSmall,
                            fontSize = 9.sp,
                            color = colors.textTertiary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bar
                        val barColor = when (selectedChartType) {
                            0 -> Primary
                            1 -> ChartCoral
                            else -> ChartCyan
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .fillMaxHeight(heightFraction.coerceIn(0.03f, 1f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            barColor,
                                            barColor.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Day label
                        Text(
                            text = dayNames.getOrElse(index) { "" },
                            style = FitPulseTypography.labelSmall,
                            fontSize = 10.sp,
                            color = colors.textTertiary
                        )
                    }
                }
            }
        } else {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Complete workouts to see your activity chart!",
                    style = FitPulseTypography.bodyMedium,
                    color = colors.textTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ============================================================================
// WEIGHT TREND CHART (Enhanced)
// ============================================================================
@Composable
fun WeightTrendChart(
    recentMeasurements: List<BodyMeasurement>,
    allMeasurements: List<BodyMeasurement>,
    latestMeasurement: BodyMeasurement?,
    colors: FitPulseColors
) {
    var selectedRange by remember { mutableIntStateOf(1) }
    val ranges = listOf("7D", "30D", "90D", "All")
    val sortedMeasurements = remember(allMeasurements) {
        allMeasurements.sortedBy { it.date }
    }

    val displayMeasurements = remember(selectedRange, recentMeasurements, sortedMeasurements) {
        when (selectedRange) {
            0 -> {
                val cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
                sortedMeasurements.filter { it.date >= cutoff }
            }
            1 -> {
                val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                sortedMeasurements.filter { it.date >= cutoff }
            }
            2 -> {
                val cutoff = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
                sortedMeasurements.filter { it.date >= cutoff }
            }
            else -> sortedMeasurements
        }
    }

    val weightData = remember(displayMeasurements) {
        displayMeasurements.mapNotNull { measurement ->
            measurement.weightKg?.let { weight -> measurement.date to weight }
        }
    }
    val averageWeight = remember(weightData) {
        weightData
            .map { it.second }
            .takeIf { it.isNotEmpty() }
            ?.average()
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weight Trend",
                style = FitPulseTypography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Range selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ranges.forEachIndexed { index, label ->
                FitPulseChip(
                    text = label,
                    selected = selectedRange == index,
                    onClick = { selectedRange = index }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Current weight info
        latestMeasurement?.weightKg?.let { current ->
            val firstInRange = weightData.firstOrNull()?.second
            val change = if (firstInRange != null) current - firstInRange else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${String.format("%.1f", current)} kg",
                        style = FitPulseTypography.titleLarge,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Current", style = FitPulseTypography.labelSmall, color = colors.textTertiary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val changeText = if (change >= 0) "+${String.format("%.1f", change)}" else String.format("%.1f", change)
                    val changeColor = if (change > 0) ChartCoral else if (change < 0) Success else colors.textSecondary
                    Text(
                        text = "$changeText kg",
                        style = FitPulseTypography.titleLarge,
                        color = changeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Change", style = FitPulseTypography.labelSmall, color = colors.textTertiary)
                }
                if (averageWeight != null && weightData.size >= 2) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format("%.1f", averageWeight)} kg",
                            style = FitPulseTypography.titleLarge,
                            color = ChartCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Average", style = FitPulseTypography.labelSmall, color = colors.textTertiary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Weight chart
        if (weightData.size >= 2) {
            val chartColor = Primary
            val weights = weightData.map { it.second }
            val minWeight = (weights.minOrNull() ?: 0f) - 2f
            val maxWeight = (weights.maxOrNull() ?: 100f) + 2f

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val padding = 40f
                val chartWidth = size.width - padding * 2
                val chartHeight = size.height - padding
                val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

                // Y-axis labels
                val steps = 4
                for (i in 0..steps) {
                    val y = padding + chartHeight * (1f - i.toFloat() / steps)
                    val weight = minWeight + weightRange * i / steps
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            "${weight.toInt()}",
                            8f,
                            y + 4f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#6B6B80")
                                textSize = 24f
                                isAntiAlias = true
                            }
                        )
                    }
                    // Grid line
                    drawLine(
                        color = Color(0xFF2A2A3A),
                        start = Offset(padding, y),
                        end = Offset(size.width - padding, y),
                        strokeWidth = 0.5f
                    )
                }

                // Build path
                val path = Path()
                val fillPath = Path()

                weightData.forEachIndexed { index, (_, weight) ->
                    val x = padding + chartWidth * index / (weightData.size - 1).coerceAtLeast(1)
                    val y = padding + chartHeight * (1f - (weight - minWeight) / weightRange)

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, padding + chartHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        // Smooth curve
                        val prevX = padding + chartWidth * (index - 1) / (weightData.size - 1).coerceAtLeast(1)
                        val prevWeight = weightData[index - 1].second
                        val prevY = padding + chartHeight * (1f - (prevWeight - minWeight) / weightRange)
                        val cx = (prevX + x) / 2
                        path.cubicTo(cx, prevY, cx, y, x, y)
                        fillPath.cubicTo(cx, prevY, cx, y, x, y)
                    }
                }

                // Close fill path
                val lastX = padding + chartWidth
                fillPath.lineTo(lastX, padding + chartHeight)
                fillPath.close()

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            chartColor.copy(alpha = 0.3f),
                            chartColor.copy(alpha = 0.0f)
                        ),
                        startY = padding,
                        endY = padding + chartHeight
                    )
                )

                // Draw line
                drawPath(
                    path = path,
                    color = chartColor,
                    style = Stroke(
                        width = 3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Draw data points
                weightData.forEachIndexed { index, (_, weight) ->
                    val x = padding + chartWidth * index / (weightData.size - 1).coerceAtLeast(1)
                    val y = padding + chartHeight * (1f - (weight - minWeight) / weightRange)

                    // Outer glow
                    drawCircle(
                        color = chartColor.copy(alpha = 0.3f),
                        radius = 8f,
                        center = Offset(x, y)
                    )
                    // Inner dot
                    drawCircle(
                        color = chartColor,
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
            }

            // X-axis date labels
            val axisDateFormatter = remember { SimpleDateFormat("M/d", Locale.getDefault()) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val indices = when {
                    weightData.size <= 5 -> weightData.indices.toList()
                    else -> listOf(0, weightData.size / 4, weightData.size / 2, 3 * weightData.size / 4, weightData.lastIndex)
                }
                indices.forEach { index ->
                    Text(
                        text = axisDateFormatter.format(Date(weightData[index].first)),
                        style = FitPulseTypography.labelSmall,
                        fontSize = 9.sp,
                        color = colors.textTertiary
                    )
                }
            }
        } else {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "Log at least 2 weight entries\nto see your trend chart",
                        style = FitPulseTypography.bodyMedium,
                        color = colors.textTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ============================================================================
// OVERVIEW STAT CARD
// ============================================================================
@Composable
private fun ProgressOverviewSection(
    totalWorkouts: Int,
    totalVolume: Float,
    maxStreak: Int
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isCompact = maxWidth < FitPulseLayout.CompactScreenBreakpoint

        if (isCompact) {
            Column(verticalArrangement = Arrangement.spacedBy(FitPulseLayout.CardSpacing)) {
                OverviewStatCard(
                    label = "Workouts",
                    value = "$totalWorkouts",
                    icon = Icons.Default.FitnessCenter,
                    color = Primary,
                    modifier = Modifier.fillMaxWidth()
                )
                OverviewStatCard(
                    label = "Volume",
                    value = Utils.formatWeight(totalVolume),
                    icon = Icons.Default.MonitorWeight,
                    color = ChartCyan,
                    modifier = Modifier.fillMaxWidth()
                )
                OverviewStatCard(
                    label = "Best Streak",
                    value = "$maxStreak",
                    icon = Icons.Default.LocalFireDepartment,
                    color = ChartCoral,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FitPulseLayout.CardSpacing)
            ) {
                OverviewStatCard(
                    label = "Workouts",
                    value = "$totalWorkouts",
                    icon = Icons.Default.FitnessCenter,
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                OverviewStatCard(
                    label = "Volume",
                    value = Utils.formatWeight(totalVolume),
                    icon = Icons.Default.MonitorWeight,
                    color = ChartCyan,
                    modifier = Modifier.weight(1f)
                )
                OverviewStatCard(
                    label = "Best Streak",
                    value = "$maxStreak",
                    icon = Icons.Default.LocalFireDepartment,
                    color = ChartCoral,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OverviewStatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val colors = FitPulseTheme.colors
    GlassCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = FitPulseTypography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = FitPulseTypography.labelSmall,
                color = colors.textTertiary
            )
        }
    }
}

@Composable
private fun ProgressStatusCard(
    status: ProgressStatusSummary,
    metrics: List<ProgressSummaryMetric>,
    colors: FitPulseColors
) {
    val accent = when (status.trajectory) {
        ProgressTrajectory.IMPROVING -> Success
        ProgressTrajectory.ON_TRACK -> ChartCyan
        ProgressTrajectory.STALLED -> Warning
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (status.trajectory) {
                        ProgressTrajectory.IMPROVING -> Icons.AutoMirrored.Filled.TrendingUp
                        ProgressTrajectory.ON_TRACK -> Icons.Default.TrackChanges
                        ProgressTrajectory.STALLED -> Icons.Default.WarningAmber
                    },
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.title,
                    style = FitPulseTypography.titleMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = status.message,
                    style = FitPulseTypography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            metrics.forEach { metric ->
                ProgressSummaryMetricCard(
                    metric = metric,
                    modifier = Modifier.weight(1f),
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun ProgressSummaryMetricCard(
    metric: ProgressSummaryMetric,
    modifier: Modifier = Modifier,
    colors: FitPulseColors
) {
    val accent = metric.tone.toneColor()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(
                text = metric.label,
                style = FitPulseTypography.labelSmall,
                color = colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.value,
                style = FitPulseTypography.titleSmall,
                color = accent,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = metric.message,
                style = FitPulseTypography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun ProgressInsightsCard(
    insights: List<ProgressInsight>,
    colors: FitPulseColors,
    onNavigateToArticle: (String) -> Unit
) {
    if (insights.isEmpty()) return
    val visibleInsights = insights.take(6)

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "What This Means",
            style = FitPulseTypography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Use the trends to make simpler decisions, not to overreact to one day.",
            style = FitPulseTypography.bodySmall,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(12.dp))

        visibleInsights.forEachIndexed { index, insight ->
            if (index > 0) {
                HorizontalDivider(color = colors.border.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(insight.tone.toneColor())
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = insight.title,
                        style = FitPulseTypography.titleSmall,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = insight.message,
                        style = FitPulseTypography.bodySmall,
                        color = colors.textSecondary
                    )
                    if (insight.relatedArticleId != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { onNavigateToArticle(insight.relatedArticleId) },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = insight.relatedArticleLabel ?: "Open in Knowledge",
                                style = FitPulseTypography.labelMedium,
                                color = Primary
                            )
                        }
                    }
                }
            }
            if (index < visibleInsights.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

private fun ProgressInsightTone.toneColor(): Color = when (this) {
    ProgressInsightTone.POSITIVE -> Success
    ProgressInsightTone.CAUTION -> Warning
    ProgressInsightTone.NEUTRAL -> ChartCyan
}

private fun measurementHistorySummary(measurement: BodyMeasurement): String {
    return listOfNotNull(
        measurement.weightKg?.let { "Weight ${Utils.formatWeight(it)} kg" },
        measurement.waistCm?.let { "Waist ${String.format("%.1f", it)} cm" },
        measurement.bodyFatPercent?.let { "Body fat ${String.format("%.1f", it)}%" }
    ).ifEmpty {
        listOf("Measurement logged")
    }.joinToString(" • ")
}

// ============================================================================
// PR CARD
// ============================================================================
@Composable
fun PRCard(
    pr: PersonalRecord,
    colors: FitPulseColors
) {
    GlassCard(
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            Text(
                text = pr.exerciseName,
                style = FitPulseTypography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (pr.recordType) {
                    RecordType.MAX_WEIGHT -> "${Utils.formatWeight(pr.value)} kg"
                    RecordType.MAX_REPS -> "${pr.value.toInt()} reps"
                    RecordType.MAX_VOLUME -> "${Utils.formatWeight(pr.value)} kg"
                    RecordType.MAX_DURATION -> "${pr.value.toInt()} min"
                    RecordType.MAX_DISTANCE -> "${Utils.formatWeight(pr.value)} km"
                },
                style = FitPulseTypography.headlineMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when (pr.recordType) {
                    RecordType.MAX_WEIGHT -> "Max Weight"
                    RecordType.MAX_REPS -> "Max Reps"
                    RecordType.MAX_VOLUME -> "Max Volume"
                    RecordType.MAX_DURATION -> "Max Duration"
                    RecordType.MAX_DISTANCE -> "Max Distance"
                },
                style = FitPulseTypography.labelSmall,
                color = colors.textTertiary
            )
            if (pr.previousValue != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Previous: ${Utils.formatWeight(pr.previousValue)}",
                    style = FitPulseTypography.labelSmall,
                    color = ChartCyan
                )
            }
        }
    }
}

// ============================================================================
// MEASUREMENT OVERVIEW
// ============================================================================
@Composable
fun MeasurementOverview(
    latestMeasurement: BodyMeasurement?,
    measurements: List<BodyMeasurement>,
    recentMeasurements: List<BodyMeasurement>,
    onDeleteMeasurement: (BodyMeasurement) -> Unit,
    colors: FitPulseColors
) {
    val measurementContext = remember(measurements) {
        buildMeasurementTrendContext(measurements)
    }
    var pendingDeleteMeasurement by remember { mutableStateOf<BodyMeasurement?>(null) }
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Body Measurements",
            style = FitPulseTypography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (latestMeasurement != null) {
            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(Date(latestMeasurement.date))

            Text(
                text = "Last recorded: $dateStr",
                style = FitPulseTypography.bodySmall,
                color = colors.textTertiary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = measurementContext.tone.toneColor().copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                    Text(
                        text = measurementContext.title,
                        style = FitPulseTypography.titleSmall,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = measurementContext.message,
                        style = FitPulseTypography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val measurementRows = listOfNotNull(
                latestMeasurement.weightKg?.let { "Weight" to "${Utils.formatWeight(it)} kg" },
                latestMeasurement.bodyFatPercent?.let { "Body Fat" to "${String.format("%.1f", it)}%" },
                latestMeasurement.chestCm?.let { "Chest" to "${String.format("%.1f", it)} cm" },
                latestMeasurement.waistCm?.let { "Waist" to "${String.format("%.1f", it)} cm" },
                latestMeasurement.hipsCm?.let { "Hips" to "${String.format("%.1f", it)} cm" },
                latestMeasurement.bicepsCm?.let { "Biceps" to "${String.format("%.1f", it)} cm" },
                latestMeasurement.thighsCm?.let { "Thighs" to "${String.format("%.1f", it)} cm" },
                latestMeasurement.neckCm?.let { "Neck" to "${String.format("%.1f", it)} cm" }
            )

            measurementRows.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rowItems.forEach { (label, value) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = value,
                                style = FitPulseTypography.titleSmall,
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = label,
                                style = FitPulseTypography.labelSmall,
                                color = colors.textTertiary
                            )
                        }
                    }
                    // Fill remaining space if odd number of items
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (recentMeasurements.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Recent Check-Ins",
                    style = FitPulseTypography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                recentMeasurements.take(4).forEachIndexed { index, measurement ->
                    Surface(
                        color = colors.card.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(measurement.date)),
                                    style = FitPulseTypography.labelMedium,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = measurementHistorySummary(measurement),
                                    style = FitPulseTypography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                            IconButton(onClick = { pendingDeleteMeasurement = measurement }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete measurement",
                                    tint = Warning
                                )
                            }
                        }
                    }

                    if (index < recentMeasurements.take(4).lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Straighten,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No measurements yet",
                        style = FitPulseTypography.bodyMedium,
                        color = colors.textTertiary
)
                    Text(
                        text = "Tap + to log your first measurement",
                        style = FitPulseTypography.bodySmall,
                        color = colors.textTertiary
                    )

                }
            }
        }
    }

    if (pendingDeleteMeasurement != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteMeasurement = null },
            containerColor = colors.surfaceElevated,
            title = {
                Text(
                    text = "Delete measurement?",
                    style = FitPulseTypography.titleLarge,
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "This removes the selected check-in from your measurement history.",
                    style = FitPulseTypography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingDeleteMeasurement?.let(onDeleteMeasurement)
                        pendingDeleteMeasurement = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Warning)
                ) {
                    Text("Delete", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteMeasurement = null }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

// ============================================================================
// LOG MEASUREMENT DIALOG
// ============================================================================
@Composable
fun MeasurementLogRoute(
    viewModel: FitPulseViewModel,
    onDismiss: () -> Unit
) {
    LogMeasurementDialog(
        onDismiss = onDismiss,
        onSave = { measurement ->
            viewModel.logMeasurement(measurement)
            onDismiss()
        }
    )
}

@Composable
fun LogMeasurementDialog(
    onDismiss: () -> Unit,
    onSave: (BodyMeasurement) -> Unit
) {
    var weightKg by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }
    var biceps by remember { mutableStateOf("") }
    var thighs by remember { mutableStateOf("") }
    var neck by remember { mutableStateOf("") }

    val colors = FitPulseTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = {
            Text(
                "Log Measurements",
                style = FitPulseTypography.titleLarge,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MeasurementField("Weight (kg)", weightKg) { weightKg = it }
                MeasurementField("Body Fat %", bodyFat) { bodyFat = it }
                MeasurementField("Chest (cm)", chest) { chest = it }
                MeasurementField("Waist (cm)", waist) { waist = it }
                MeasurementField("Hips (cm)", hips) { hips = it }
                MeasurementField("Biceps (cm)", biceps) { biceps = it }
                MeasurementField("Thighs (cm)", thighs) { thighs = it }
                MeasurementField("Neck (cm)", neck) { neck = it }
            }
        },
        confirmButton = {
            GradientButton(
                text = "Save",
                onClick = {
                    val measurement = BodyMeasurement(
                        date = System.currentTimeMillis(),
                        weightKg = weightKg.toFloatOrNull(),
                        bodyFatPercent = bodyFat.toFloatOrNull(),
                        chestCm = chest.toFloatOrNull(),
                        waistCm = waist.toFloatOrNull(),
                        hipsCm = hips.toFloatOrNull(),
                        bicepsCm = biceps.toFloatOrNull(),
                        thighsCm = thighs.toFloatOrNull(),
                        neckCm = neck.toFloatOrNull()
                    )
                    onSave(measurement)
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.textSecondary)
            }
        }
    )
}

@Composable
fun MeasurementField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val colors = FitPulseTheme.colors
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = colors.textTertiary) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = colors.border,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = Primary
        )
    )
}














