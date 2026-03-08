package com.fitpulse.pro.ui.screens.nutrition

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.HapticHelper
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FitPulseViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val todayCalories by viewModel.todayCalories.collectAsState()
    val todayProtein by viewModel.todayProtein.collectAsState()
    val todayCarbs by viewModel.todayCarbs.collectAsState()
    val todayFat by viewModel.todayFat.collectAsState()
    val todayWater by viewModel.todayWater.collectAsState()
    val todayWaterEntries by viewModel.todayWaterEntries.collectAsState()
    val weeklyMeals by viewModel.weeklyMeals.collectAsState()
    val weeklyWaterEntries by viewModel.weeklyWaterEntries.collectAsState()

    val calorieGoal = profile?.dailyCalorieGoal ?: 2000
    val proteinGoal = profile?.dailyProteinGoal ?: 150
    val carbsGoal = profile?.dailyCarbsGoal ?: 250
    val fatGoal = profile?.dailyFatGoal ?: 65
    val waterGoalMl = profile?.dailyWaterGoalMl ?: 3000
    val consumedCalories = todayCalories ?: 0
    val consumedProtein = todayProtein ?: 0f
    val consumedCarbs = todayCarbs ?: 0f
    val consumedFat = todayFat ?: 0f
    val consumedWater = todayWater ?: 0
    val mealTemplates = remember { nutritionMealTemplates() }
    val dailyGuidance = remember(
        consumedCalories,
        calorieGoal,
        consumedProtein,
        proteinGoal,
        consumedWater,
        waterGoalMl,
        todayMeals.size
    ) {
        buildDailyNutritionGuidance(
            calories = consumedCalories,
            calorieGoal = calorieGoal,
            proteinGrams = consumedProtein,
            proteinGoal = proteinGoal,
            waterMl = consumedWater,
            waterGoalMl = waterGoalMl,
            mealsLogged = todayMeals.size
        )
    }
    val weeklySummary = remember(
        weeklyMeals,
        weeklyWaterEntries,
        calorieGoal,
        proteinGoal,
        waterGoalMl
    ) {
        buildWeeklyNutritionSummary(
            meals = weeklyMeals,
            waterEntries = weeklyWaterEntries,
            calorieGoal = calorieGoal,
            proteinGoal = proteinGoal,
            waterGoalMl = waterGoalMl
        )
    }

    var showAddMealDialog by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }

    if (showAddMealDialog) {
        AddMealDialog(
            mealType = selectedMealType,
            onDismiss = { showAddMealDialog = false },
            onSave = { meal ->
                viewModel.logMeal(meal)
                showAddMealDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .testTag(TestTags.NutritionScreen),
        contentPadding = PaddingValues(bottom = FitPulseLayout.ScreenBottomPadding)
    ) {
        // Header
        item {
            Text(
                text = "Nutrition",
                style = FitPulseTypography.displayMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = FitPulseLayout.ScreenHorizontalPadding,
                    top = FitPulseLayout.ScreenHeaderTopPadding,
                    bottom = 16.dp
                )
            )
        }

        // Calorie Summary Card
        item {
            val remaining = calorieGoal - consumedCalories
            NutritionCalorieSummaryCard(
                consumedCalories = consumedCalories,
                calorieGoal = calorieGoal,
                remainingCalories = remaining
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            DailyNutritionGuidanceCard(
                guidance = dailyGuidance,
                calories = consumedCalories,
                calorieGoal = calorieGoal,
                proteinGrams = consumedProtein,
                proteinGoal = proteinGoal,
                waterMl = consumedWater,
                waterGoalMl = waterGoalMl
            )
        }

        // Animated Macro Rings
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Macros")
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MacroRing(label = "Protein", current = consumedProtein, goal = proteinGoal.toFloat(), color = ChartPurple, unit = "g")
                    MacroRing(label = "Carbs", current = consumedCarbs, goal = carbsGoal.toFloat(), color = ChartCyan, unit = "g")
                    MacroRing(label = "Fat", current = consumedFat, goal = fatGoal.toFloat(), color = ChartOrange, unit = "g")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Meal Templates")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mealTemplates, key = { it.id }) { template ->
                    MealTemplateCard(
                        template = template,
                        onClick = {
                            val now = System.currentTimeMillis()
                            viewModel.logMeal(
                                template.meal.copy(
                                    id = 0,
                                    date = now,
                                    createdAt = now
                                )
                            )
                        }
                    )
                }
            }
        }

        // Quick-Add Presets
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Quick Add")
            val quickPresets = listOf(
                Triple("🥤 Protein Shake", 150, Triple(30f, 5f, 3f)),
                Triple("🍗 Chicken Breast", 165, Triple(31f, 0f, 3.6f)),
                Triple("🍚 Rice Bowl", 210, Triple(4f, 45f, 0.5f)),
                Triple("🍌 Banana", 105, Triple(1.3f, 27f, 0.4f)),
                Triple("🥚 2 Eggs", 143, Triple(13f, 1f, 10f)),
                Triple("🥜 Almonds (30g)", 170, Triple(6f, 6f, 15f))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quickPresets.forEach { (name, cals, macros) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.7f)),
                        border = CardDefaults.outlinedCardBorder().let {
                            androidx.compose.foundation.BorderStroke(1.dp, Border.copy(alpha = 0.2f))
                        },
                        onClick = {
                            viewModel.logMeal(
                                MealEntry(
                                    name = name.substringAfter(" "),
                                    mealType = MealType.SNACK,
                                    calories = cals,
                                    proteinGrams = macros.first,
                                    carbsGrams = macros.second,
                                    fatGrams = macros.third
                                )
                            )
                        },
                        modifier = Modifier.width(130.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(name.substringBefore(" "), style = FitPulseTypography.headlineSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                name.substringAfter(" "),
                                style = FitPulseTypography.labelMedium,
                                color = FitPulseTheme.colors.textPrimary,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Text(
                                "$cals cal",
                                style = FitPulseTypography.labelSmall,
                                color = Accent
                            )
                        }
                    }
                }
            }
        }

        // Water Tracker — animated bottle
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Hydration")
            WaterBottleCard(
                currentMl = consumedWater,
                goalMl = waterGoalMl,
                waterEntryCount = todayWaterEntries.size,
                onAddWater = { amount -> viewModel.addWater(amount) },
                onUndoLast = { viewModel.undoLastWater() },
                onResetToday = { viewModel.clearTodayWater() }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Weekly Consistency")
            WeeklyNutritionConsistencyCard(summary = weeklySummary)
        }

        // Meal Log Sections
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(title = "Today's Meals")
        }

        val mealTypes = listOf(
            MealType.BREAKFAST to "🌅 Breakfast",
            MealType.LUNCH to "☀️ Lunch",
            MealType.DINNER to "🌙 Dinner",
            MealType.SNACK to "🍎 Snack",
            MealType.PRE_WORKOUT to "⚡ Pre-Workout",
            MealType.POST_WORKOUT to "💪 Post-Workout"
        )

        mealTypes.forEach { (mealType, label) ->
            item {
                MealSection(
                    label = label,
                    meals = todayMeals.filter { it.mealType == mealType },
                    onAddMeal = {
                        selectedMealType = mealType
                        showAddMealDialog = true
                    },
                    onDeleteMeal = { viewModel.deleteMeal(it) }
                )
            }
        }
    }
}

@Composable
private fun NutritionCalorieSummaryCard(
    consumedCalories: Int,
    calorieGoal: Int,
    remainingCalories: Int
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding)
    ) {
        val compactLayout = maxWidth < FitPulseLayout.MediumScreenBreakpoint
        val progress = if (calorieGoal > 0) consumedCalories.toFloat() / calorieGoal else 0f

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            if (compactLayout) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(FitPulseLayout.CardSpacing)
                ) {
                    MacroHeadlineStat(
                        label = "Eaten",
                        value = "$consumedCalories",
                        color = Primary
                    )
                    CircularProgressRing(
                        progress = progress,
                        size = 130.dp,
                        strokeWidth = 14.dp,
                        progressColor = if (remainingCalories >= 0) Primary else Error
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${kotlin.math.abs(remainingCalories)}",
                                style = FitPulseTypography.headlineLarge,
                                color = if (remainingCalories >= 0) TextPrimary else Error,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (remainingCalories >= 0) "Remaining" else "Over",
                                style = FitPulseTypography.labelSmall,
                                color = FitPulseTheme.colors.textTertiary
                            )
                        }
                    }
                    MacroHeadlineStat(
                        label = "Goal",
                        value = "$calorieGoal",
                        color = Success
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MacroHeadlineStat(
                        label = "Eaten",
                        value = "$consumedCalories",
                        color = Primary
                    )
                    CircularProgressRing(
                        progress = progress,
                        size = 130.dp,
                        strokeWidth = 14.dp,
                        progressColor = if (remainingCalories >= 0) Primary else Error
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${kotlin.math.abs(remainingCalories)}",
                                style = FitPulseTypography.headlineLarge,
                                color = if (remainingCalories >= 0) TextPrimary else Error,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                if (remainingCalories >= 0) "Remaining" else "Over",
                                style = FitPulseTypography.labelSmall,
                                color = FitPulseTheme.colors.textTertiary
                            )
                        }
                    }
                    MacroHeadlineStat(
                        label = "Goal",
                        value = "$calorieGoal",
                        color = Success
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroHeadlineStat(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = FitPulseTypography.headlineLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = FitPulseTypography.labelMedium,
            color = FitPulseTheme.colors.textTertiary
        )
    }
}

@Composable
private fun DailyNutritionGuidanceCard(
    guidance: DailyNutritionGuidance,
    calories: Int,
    calorieGoal: Int,
    proteinGrams: Float,
    proteinGoal: Int,
    waterMl: Int,
    waterGoalMl: Int
) {
    val accentColor = when (guidance.tone) {
        NutritionGuidanceTone.POSITIVE -> Success
        NutritionGuidanceTone.NEUTRAL -> Primary
        NutritionGuidanceTone.CAUTION -> Warning
    }

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
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = guidance.title,
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = guidance.message,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NutritionGoalChip(
                label = "Calories",
                value = "$calories/$calorieGoal",
                isMet = calorieGoal > 0 && calories in (calorieGoal * 0.8f).toInt()..(calorieGoal * 1.1f).toInt(),
                modifier = Modifier.weight(1f)
            )
            NutritionGoalChip(
                label = "Protein",
                value = "${proteinGrams.toInt()}/${proteinGoal}g",
                isMet = proteinGoal > 0 && proteinGrams >= proteinGoal * 0.8f,
                modifier = Modifier.weight(1f)
            )
            NutritionGoalChip(
                label = "Water",
                value = "${Utils.formatHydrationAmount(waterMl)}/${Utils.formatHydrationAmount(waterGoalMl)}",
                isMet = waterGoalMl > 0 && waterMl >= waterGoalMl * 0.75f,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Next: ${guidance.nextAction}",
            style = FitPulseTypography.labelMedium,
            color = accentColor
        )
    }
}

@Composable
private fun NutritionGoalChip(
    label: String,
    value: String,
    isMet: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = if (isMet) Success else Warning
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = accentColor.copy(alpha = 0.12f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)) {
            Text(
                text = label,
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = FitPulseTypography.labelMedium,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MealTemplateCard(
    template: MealTemplatePreset,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(188.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, Border.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = template.title,
                style = FitPulseTypography.titleMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = template.subtitle,
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textSecondary,
                minLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${template.meal.calories} cal  |  ${template.meal.proteinGrams.toInt()}g protein",
                style = FitPulseTypography.labelMedium,
                color = Primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = template.meal.mealType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                style = FitPulseTypography.labelSmall,
                color = FitPulseTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun WeeklyNutritionConsistencyCard(
    summary: WeeklyNutritionSummary
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = summary.headline,
            style = FitPulseTypography.titleMedium,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summary.message,
            style = FitPulseTypography.bodySmall,
            color = FitPulseTheme.colors.textSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NutritionGoalChip(
                label = "Calories",
                value = "${summary.calorieGoalDays}/7 days",
                isMet = summary.calorieGoalDays >= 4,
                modifier = Modifier.weight(1f)
            )
            NutritionGoalChip(
                label = "Protein",
                value = "${summary.proteinGoalDays}/7 days",
                isMet = summary.proteinGoalDays >= 4,
                modifier = Modifier.weight(1f)
            )
            NutritionGoalChip(
                label = "Water",
                value = "${summary.waterGoalDays}/7 days",
                isMet = summary.waterGoalDays >= 4,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            summary.days.forEach { day ->
                WeeklyNutritionDayCard(day = day)
            }
        }
    }
}

@Composable
private fun WeeklyNutritionDayCard(
    day: WeeklyNutritionDaySummary
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = FitPulseTheme.colors.card.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, Border.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.label,
                style = FitPulseTypography.labelMedium,
                color = FitPulseTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                WeeklyMetricDot(label = "C", isMet = day.calorieGoalHit)
                WeeklyMetricDot(label = "P", isMet = day.proteinGoalHit)
                WeeklyMetricDot(label = "W", isMet = day.waterGoalHit)
            }
        }
    }
}

@Composable
private fun WeeklyMetricDot(
    label: String,
    isMet: Boolean
) {
    val accentColor = if (isMet) Success else FitPulseTheme.colors.textTertiary
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = if (isMet) 0.2f else 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = FitPulseTypography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MealSection(
    label: String,
    meals: List<MealEntry>,
    onAddMeal: () -> Unit,
    onDeleteMeal: (MealEntry) -> Unit
) {
    val totalCalories = meals.sumOf { it.calories }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (totalCalories > 0) {
                    Text("$totalCalories cal", style = FitPulseTypography.labelMedium, color = Accent)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                IconButton(
                    onClick = onAddMeal,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Primary.copy(alpha = 0.15f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                }
            }
        }

        if (meals.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No meals logged here yet. Use a template or add one manually.",
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textTertiary
            )
        }

        meals.forEach { meal ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(meal.name, style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textPrimary)
                    Text(
                        "P: ${meal.proteinGrams.toInt()}g | C: ${meal.carbsGrams.toInt()}g | F: ${meal.fatGrams.toInt()}g",
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
                Text("${meal.calories} cal", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textPrimary)
                IconButton(onClick = { onDeleteMeal(meal) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = FitPulseTheme.colors.textTertiary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun AddMealDialog(
    mealType: MealType,
    onDismiss: () -> Unit,
    onSave: (MealEntry) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FitPulseTheme.colors.surface,
        title = { Text("Add ${mealType.name.lowercase().replaceFirstChar { it.uppercase() }}", color = FitPulseTheme.colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NutritionTextField(value = name, onValueChange = { name = it }, label = "Food Name", isNumber = false)
                NutritionTextField(value = calories, onValueChange = { calories = it }, label = "Calories")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NutritionTextField(value = protein, onValueChange = { protein = it }, label = "Protein (g)", modifier = Modifier.weight(1f))
                    NutritionTextField(value = carbs, onValueChange = { carbs = it }, label = "Carbs (g)", modifier = Modifier.weight(1f))
                    NutritionTextField(value = fat, onValueChange = { fat = it }, label = "Fat (g)", modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = "Save",
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            MealEntry(
                                name = name,
                                mealType = mealType,
                                calories = calories.toIntOrNull() ?: 0,
                                proteinGrams = protein.toFloatOrNull() ?: 0f,
                                carbsGrams = carbs.toFloatOrNull() ?: 0f,
                                fatGrams = fat.toFloatOrNull() ?: 0f
                            )
                        )
                    }
                },
                enabled = name.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = FitPulseTheme.colors.textTertiary) }
        }
    )
}

@Composable
private fun NutritionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isNumber: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (!isNumber || it.all { c -> c.isDigit() || c == '.' }) onValueChange(it) },
        label = { Text(label, style = FitPulseTypography.labelSmall) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Border,
            focusedTextColor = FitPulseTheme.colors.textPrimary,
            unfocusedTextColor = FitPulseTheme.colors.textPrimary,
            cursorColor = Primary,
            focusedLabelColor = Primary,
            unfocusedLabelColor = FitPulseTheme.colors.textTertiary
        )
    )
}

@Composable
private fun MacroRing(
    label: String,
    current: Float,
    goal: Float,
    color: Color,
    unit: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (goal > 0) (current / goal).coerceAtMost(1.2f) else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "$label progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressRing(
            progress = animatedProgress,
            size = 80.dp,
            strokeWidth = 8.dp,
            progressColor = color,
            backgroundColor = color.copy(alpha = 0.1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${current.toInt()}",
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    unit,
                    style = FitPulseTypography.labelSmall,
                    color = FitPulseTheme.colors.textTertiary
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, style = FitPulseTypography.labelMedium, color = color, fontWeight = FontWeight.SemiBold)
        Text(
            "${(goal - current).coerceAtLeast(0f).toInt()}$unit left",
            style = FitPulseTypography.labelSmall,
            color = FitPulseTheme.colors.textTertiary
        )
    }
}

// ============================================================================
// ANIMATED WATER BOTTLE CARD
// ============================================================================
@Composable
private fun WaterBottleCard(
    currentMl: Int,
    goalMl: Int,
    waterEntryCount: Int,
    onAddWater: (Int) -> Unit,
    onUndoLast: () -> Unit,
    onResetToday: () -> Unit
) {
    val view = LocalView.current
    val fillFraction = if (goalMl > 0) (currentMl.toFloat() / goalMl).coerceIn(0f, 1f) else 0f
    val hydrationSummary = buildString {
        append("${currentMl / 250} glasses")
        if (goalMl > 0) {
            val delta = goalMl - currentMl
            append(" • ")
            append(
                if (delta >= 0) {
                    "${Utils.formatHydrationAmount(delta)} left"
                } else {
                    "${Utils.formatHydrationAmount(-delta)} over goal"
                }
            )
        }
    }

    // Animated fill level
    val animatedFill by animateFloatAsState(
        targetValue = fillFraction,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "water_fill"
    )

    // Wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    val waterColor = Secondary
    val waterColorDark = Secondary.copy(alpha = 0.7f)
    val isSuspiciouslyHigh = goalMl > 0 && currentMl >= goalMl * 2

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Animated bottle
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val neckRatio = 0.3f
                    val neckH = h * 0.18f
                    val bodyH = h - neckH
                    val neckW = w * neckRatio
                    val cornerR = 12f

                    // Bottle outline
                    val bottlePath = Path().apply {
                        // Start at top-left of neck
                        moveTo((w - neckW) / 2, 0f)
                        lineTo((w + neckW) / 2, 0f)
                        lineTo((w + neckW) / 2, neckH * 0.6f)
                        // Shoulder widening
                        lineTo(w - cornerR, neckH)
                        // Right side body
                        lineTo(w - cornerR, h - cornerR)
                        // Bottom-right corner
                        quadraticTo(w, h - cornerR, w, h - cornerR + cornerR * 0.5f)
                        quadraticTo(w, h, w - cornerR, h)
                        // Bottom
                        lineTo(cornerR, h)
                        // Bottom-left corner
                        quadraticTo(0f, h, 0f, h - cornerR + cornerR * 0.5f)
                        quadraticTo(0f, h - cornerR, cornerR, h - cornerR)
                        // Left side body
                        lineTo(cornerR, neckH)
                        // Shoulder
                        lineTo((w - neckW) / 2, neckH * 0.6f)
                        close()
                    }

                    // Draw bottle outline
                    drawPath(
                        path = bottlePath,
                        color = waterColor.copy(alpha = 0.3f),
                        style = Stroke(width = 2f)
                    )

                    // Fill: from bottom up to animatedFill * bodyH
                    val fillTop = h - (animatedFill * bodyH)
                    if (animatedFill > 0.01f) {
                        val fillPath = Path().apply {
                            // Wave at the top of fill
                            val waveAmplitude = 3f * animatedFill.coerceAtMost(0.9f)
                            moveTo(cornerR, fillTop + (sin(wavePhase.toDouble()) * waveAmplitude).toFloat())
                            // Draw wave across
                            val steps = 20
                            for (i in 1..steps) {
                                val x = cornerR + (w - 2 * cornerR) * i / steps
                                val y = fillTop + (sin(wavePhase + i * 0.5f) * waveAmplitude).toFloat()
                                lineTo(x, y)
                            }
                            // Right side down
                            lineTo(w - cornerR, h - cornerR)
                            quadraticTo(w, h - cornerR, w, h - cornerR + cornerR * 0.5f)
                            quadraticTo(w, h, w - cornerR, h)
                            lineTo(cornerR, h)
                            quadraticTo(0f, h, 0f, h - cornerR + cornerR * 0.5f)
                            quadraticTo(0f, h - cornerR, cornerR, h - cornerR)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(waterColor.copy(alpha = 0.5f), waterColorDark),
                                startY = fillTop,
                                endY = h
                            ),
                            style = Fill
                        )
                    }
                }

                // Percentage text on bottle
                Text(
                    text = "${(animatedFill * 100).toInt()}%",
                    style = FitPulseTypography.labelSmall,
                    color = if (animatedFill > 0.5f) Color.White else waterColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    Utils.formatHydrationAmount(currentMl),
                    style = FitPulseTypography.headlineMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "of ${Utils.formatHydrationAmount(goalMl)} goal",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    hydrationSummary,
                    style = FitPulseTypography.labelSmall,
                    color = FitPulseTheme.colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isSuspiciouslyHigh) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "This total looks unusually high. Use Undo or Reset if a log was accidental.",
                        style = FitPulseTypography.bodySmall,
                        color = Warning
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick-add preset buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(150 to "150ml", 250 to "250ml", 500 to "500ml", 750 to "750ml").forEach { (amount, label) ->
                        FilledTonalButton(
                            onClick = {
                                HapticHelper.confirm(view)
                                onAddWater(amount)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Secondary.copy(alpha = 0.12f),
                                contentColor = Secondary
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(30.dp)
                        ) {
                            Text(label, style = FitPulseTypography.labelSmall)
                        }
                    }
                }

                if (waterEntryCount > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticHelper.tick(view)
                                onUndoLast()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary)
                        ) {
                            Text("Undo Last", style = FitPulseTypography.labelMedium)
                        }
                        OutlinedButton(
                            onClick = {
                                HapticHelper.tick(view)
                                onResetToday()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Warning)
                        ) {
                            Text("Reset Today", style = FitPulseTypography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
