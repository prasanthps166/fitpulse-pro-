package com.fitpulse.pro.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.components.GradientButton
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: FitPulseViewModel,
    onComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("25") }
    var heightCm by remember { mutableStateOf("170") }
    var weightKg by remember { mutableStateOf("70") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var selectedGoal by remember { mutableStateOf(FitnessGoal.STAY_FIT) }
    var selectedActivity by remember { mutableStateOf(ActivityLevel.MODERATE) }

    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .testTag(TestTags.OnboardingScreen)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> PersonalInfoPage(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    onEmailChange = { email = it },
                    age = age,
                    onAgeChange = { age = it },
                    selectedGender = selectedGender,
                    onGenderChange = { selectedGender = it }
                )
                2 -> BodyMetricsPage(
                    heightCm = heightCm,
                    onHeightChange = { heightCm = it },
                    weightKg = weightKg,
                    onWeightChange = { weightKg = it }
                )
                3 -> GoalPage(
                    selectedGoal = selectedGoal,
                    onGoalChange = { selectedGoal = it },
                    selectedActivity = selectedActivity,
                    onActivityChange = { selectedActivity = it }
                )
            }
        }

        // Bottom navigation section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width = if (pagerState.currentPage == index) 24.dp else 8.dp,
                                height = 8.dp
                            )
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (pagerState.currentPage == index) Primary
                                else FitPulseTheme.colors.textTertiary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientButton(
                text = if (pagerState.currentPage == 3) "Get Started" else "Next",
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < 3) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Save profile and complete onboarding
                            val profile = UserProfile(
                                name = name.ifBlank { "Athlete" },
                                email = email.trim(),
                                age = age.toIntOrNull() ?: 25,
                                gender = selectedGender,
                                heightCm = heightCm.toFloatOrNull() ?: 170f,
                                weightKg = weightKg.toFloatOrNull() ?: 70f,
                                fitnessGoal = selectedGoal,
                                activityLevel = selectedActivity,
                                hasCompletedOnboarding = true
                            )
                            viewModel.saveProfile(profile)
                            onComplete()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(TestTags.OnboardingPrimaryButton)
            )

            if (pagerState.currentPage < 3) {
                TextButton(
                    onClick = {
                        val profile = UserProfile(email = email.trim(), hasCompletedOnboarding = true)
                        viewModel.saveProfile(profile)
                        onComplete()
                    },
                    modifier = Modifier.testTag(TestTags.OnboardingSkipButton)
                ) {
                    Text("Skip", color = FitPulseTheme.colors.textTertiary)
                }
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App icon with glow effect
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = pulseAlpha),
                            GradientEnd.copy(alpha = pulseAlpha * 0.5f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Primary, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "FitPulse Pro",
            style = FitPulseTypography.displayLarge,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your Ultimate Fitness Companion",
            style = FitPulseTypography.bodyLarge,
            color = FitPulseTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Feature highlights
        FeatureHighlight(Icons.Default.FitnessCenter, "Smart Workout Tracking", "AI-powered personalized plans")
        Spacer(modifier = Modifier.height(16.dp))
        FeatureHighlight(Icons.Default.Restaurant, "Nutrition Logging", "Track macros and calories effortlessly")
        Spacer(modifier = Modifier.height(16.dp))
        FeatureHighlight(Icons.AutoMirrored.Filled.TrendingUp, "Progress Analytics", "Visualize your fitness journey")
        Spacer(modifier = Modifier.height(16.dp))
        FeatureHighlight(Icons.Default.School, "Learning Hub", "Exercise guides and fitness knowledge")

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FeatureHighlight(icon: ImageVector, title: String, subtitle: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
            Text(subtitle, style = FitPulseTypography.bodySmall, color = FitPulseTheme.colors.textSecondary)
        }
    }
}

@Composable
private fun PersonalInfoPage(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    selectedGender: Gender,
    onGenderChange: (Gender) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tell us about yourself", style = FitPulseTypography.displaySmall, color = FitPulseTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedTextColor = FitPulseTheme.colors.textPrimary,
                unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                focusedLabelColor = Primary,
                unfocusedLabelColor = FitPulseTheme.colors.textTertiary,
                cursorColor = Primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email (optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedTextColor = FitPulseTheme.colors.textPrimary,
                unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                focusedLabelColor = Primary,
                unfocusedLabelColor = FitPulseTheme.colors.textTertiary,
                cursorColor = Primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = age,
            onValueChange = { if (it.length <= 3) onAgeChange(it.filter { c -> c.isDigit() }) },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border,
                focusedTextColor = FitPulseTheme.colors.textPrimary,
                unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                focusedLabelColor = Primary,
                unfocusedLabelColor = FitPulseTheme.colors.textTertiary,
                cursorColor = Primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Gender", style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Gender.values().forEach { gender ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedGender == gender) Primary.copy(alpha = 0.2f) else FitPulseTheme.colors.card,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedGender == gender) Primary else Border
                    ),
                    onClick = { onGenderChange(gender) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = gender.name.lowercase().replaceFirstChar { it.uppercase() },
                            color = if (selectedGender == gender) Primary else FitPulseTheme.colors.textSecondary,
                            style = FitPulseTypography.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun BodyMetricsPage(
    heightCm: String,
    onHeightChange: (String) -> Unit,
    weightKg: String,
    onWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.MonitorWeight,
            contentDescription = null,
            tint = Secondary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Body Metrics", style = FitPulseTypography.displaySmall, color = FitPulseTheme.colors.textPrimary)
        Text("Help us personalize your experience", style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textSecondary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = heightCm,
            onValueChange = { if (it.length <= 3) onHeightChange(it.filter { c -> c.isDigit() }) },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Secondary,
                unfocusedBorderColor = Border,
                focusedTextColor = FitPulseTheme.colors.textPrimary,
                unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                focusedLabelColor = Secondary,
                unfocusedLabelColor = FitPulseTheme.colors.textTertiary,
                cursorColor = Secondary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = weightKg,
            onValueChange = { if (it.length <= 5) onWeightChange(it.filter { c -> c.isDigit() || c == '.' }) },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Secondary,
                unfocusedBorderColor = Border,
                focusedTextColor = FitPulseTheme.colors.textPrimary,
                unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                focusedLabelColor = Secondary,
                unfocusedLabelColor = FitPulseTheme.colors.textTertiary,
                cursorColor = Secondary
            ),
            singleLine = true
        )

        // BMI Preview
        val h = heightCm.toFloatOrNull() ?: 170f
        val w = weightKg.toFloatOrNull() ?: 70f
        if (h > 0 && w > 0) {
            val bmi = w / ((h / 100f) * (h / 100f))
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your BMI", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textTertiary)
                    Text(
                        text = String.format("%.1f", bmi),
                        style = FitPulseTypography.statValue,
                        color = when {
                            bmi < 18.5f -> Warning
                            bmi < 25f -> Success
                            bmi < 30f -> Warning
                            else -> Error
                        }
                    )
                    Text(
                        text = when {
                            bmi < 18.5f -> "Underweight"
                            bmi < 25f -> "Normal Weight"
                            bmi < 30f -> "Overweight"
                            else -> "Obese"
                        },
                        style = FitPulseTypography.bodySmall,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun GoalPage(
    selectedGoal: FitnessGoal,
    onGoalChange: (FitnessGoal) -> Unit,
    selectedActivity: ActivityLevel,
    onActivityChange: (ActivityLevel) -> Unit
) {
    val goals = listOf(
        Triple(FitnessGoal.LOSE_WEIGHT, Icons.AutoMirrored.Filled.TrendingDown, "Lose Weight"),
        Triple(FitnessGoal.BUILD_MUSCLE, Icons.Default.FitnessCenter, "Build Muscle"),
        Triple(FitnessGoal.STAY_FIT, Icons.Default.Favorite, "Stay Fit"),
        Triple(FitnessGoal.IMPROVE_ENDURANCE, Icons.AutoMirrored.Filled.DirectionsRun, "Improve Endurance"),
        Triple(FitnessGoal.INCREASE_FLEXIBILITY, Icons.Default.SelfImprovement, "Increase Flexibility")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("What's your goal?", style = FitPulseTypography.displaySmall, color = FitPulseTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(24.dp))

        goals.forEach { (goal, icon, label) ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (selectedGoal == goal) Primary.copy(alpha = 0.15f) else FitPulseTheme.colors.card,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (selectedGoal == goal) Primary else Border
                ),
                onClick = { onGoalChange(goal) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, tint = if (selectedGoal == goal) Primary else FitPulseTheme.colors.textSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        label,
                        style = FitPulseTypography.titleLarge,
                        color = if (selectedGoal == goal) Primary else FitPulseTheme.colors.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Activity Level", style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        val activities = listOf(
            ActivityLevel.SEDENTARY to "Sedentary",
            ActivityLevel.LIGHT to "Light",
            ActivityLevel.MODERATE to "Moderate",
            ActivityLevel.ACTIVE to "Active",
            ActivityLevel.VERY_ACTIVE to "Very Active"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            activities.forEach { (level, label) ->
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedActivity == level) Primary.copy(alpha = 0.2f) else FitPulseTheme.colors.card,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedActivity == level) Primary else Border
                    ),
                    onClick = { onActivityChange(level) }
                ) {
                    Text(
                        text = label,
                        style = FitPulseTypography.labelSmall,
                        color = if (selectedActivity == level) Primary else FitPulseTheme.colors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}



