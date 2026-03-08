package com.fitpulse.pro.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: FitPulseViewModel,
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAchievements: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val totalWorkouts by viewModel.totalWorkoutCount.collectAsState()
    val totalVolume by viewModel.totalVolume.collectAsState()
    val totalCalories by viewModel.totalCaloriesBurned.collectAsState()
    val maxStreak by viewModel.maxStreak.collectAsState()
    val unlockedAchievements by viewModel.unlockedAchievementCount.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatar(uri.toString())
        }
    }

    if (showEditDialog && profile != null) {
        EditProfileDialog(
            profile = profile!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedProfile ->
                viewModel.saveProfile(updatedProfile)
                showEditDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Primary.copy(alpha = 0.15f), Color.Transparent),
                            startY = 0f,
                            endY = 400f
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = FitPulseTheme.colors.textPrimary)
                        }
                        Row {
                            IconButton(onClick = { showEditDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Primary)
                            }
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = FitPulseTheme.colors.textSecondary)
                            }
                        }
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(listOf(Primary, GradientEnd))
                            )
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profile?.avatarUri != null) {
                            AsyncImage(
                                model = profile!!.avatarUri,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = (profile?.name?.firstOrNull()?.uppercase() ?: "A"),
                                style = FitPulseTypography.displayLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Edit Icon Overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FitPulseTheme.colors.card)
                                .border(2.dp, FitPulseTheme.colors.background, CircleShape)
                                .padding(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Photo",
                                tint = Primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        profile?.name?.ifBlank { "Athlete" } ?: "Athlete",
                        style = FitPulseTypography.displaySmall,
                        color = FitPulseTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        profile?.fitnessGoal?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Stay Fit",
                        style = FitPulseTypography.bodyMedium,
                        color = FitPulseTheme.colors.textSecondary
                    )
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStat("Workouts", "$totalWorkouts", Primary, Modifier.weight(1f))
                ProfileStat("Volume", "${Utils.formatNumber((totalVolume ?: 0f).toInt())}kg", Secondary, Modifier.weight(1f))
                ProfileStat("Calories", Utils.formatNumber(totalCalories ?: 0), Accent, Modifier.weight(1f))
                ProfileStat("Streak", "${maxStreak ?: 0}d", Warning, Modifier.weight(1f))
            }
        }

        // Profile Details
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Profile Details")

            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                val details = listOfNotNull(
                    profile?.email?.takeIf { it.isNotBlank() }?.let { "Email" to it },
                    "Age" to "${profile?.age ?: 25} years",
                    "Gender" to (profile?.gender?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Male"),
                    "Height" to Utils.formatHeight(profile?.heightCm ?: 170f),
                    "Weight" to Utils.formatWeight(profile?.weightKg ?: 70f),
                    "Activity" to (profile?.activityLevel?.name?.replace("_", " ")?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Moderate"),
                    "BMI" to Utils.calculateBMI(profile?.weightKg ?: 70f, profile?.heightCm ?: 170f).let { String.format("%.1f (%s)", it, Utils.getBMICategory(it)) }
                )

                details.forEachIndexed { index, (label, value) ->
                    if (index > 0) HorizontalDivider(color = Border.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textSecondary)
                        Text(value, style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Goals
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Daily Goals")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GoalCard("Calories", "${profile?.dailyCalorieGoal ?: 2000}", "kcal", Primary, Modifier.weight(1f))
                GoalCard("Protein", "${profile?.dailyProteinGoal ?: 150}", "g", ChartPurple, Modifier.weight(1f))
                GoalCard("Water", "${(profile?.dailyWaterGoalMl ?: 3000) / 1000}", "L", Secondary, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GoalCard("Carbs", "${profile?.dailyCarbsGoal ?: 250}", "g", ChartCyan, Modifier.weight(1f))
                GoalCard("Fat", "${profile?.dailyFatGoal ?: 65}", "g", ChartOrange, Modifier.weight(1f))
                GoalCard("Steps", "${Utils.formatNumber(profile?.dailyStepsGoal ?: 10000)}", "", Success, Modifier.weight(1f))
            }
        }

        // Achievements Preview
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Achievements", actionText = "See All", onAction = onNavigateToAchievements)

            GradientCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = listOf(Warning.copy(alpha = 0.15f), Accent.copy(alpha = 0.1f)),
                onClick = onNavigateToAchievements
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Warning, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("$unlockedAchievements achievements unlocked", style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary)
                        Text("Keep pushing to unlock more!", style = FitPulseTypography.bodySmall, color = FitPulseTheme.colors.textSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FitPulseTheme.colors.textTertiary)
                }
            }
        }

        // Quick Actions
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(title = "Quick Actions")

            val actions = listOf(
                Triple(Icons.Default.Settings, "Settings", onNavigateToSettings),
                Triple(Icons.Default.EmojiEvents, "Achievements", onNavigateToAchievements)
            )

            actions.forEach { (icon, label, action) ->
                ProfileActionItem(icon = icon, label = label, onClick = action)
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = FitPulseTypography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary)
        }
    }
}

@Composable
private fun GoalCard(label: String, value: String, unit: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = FitPulseTypography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
            Text("$unit $label".trim(), style = FitPulseTypography.labelSmall, color = FitPulseTheme.colors.textTertiary)
        }
    }
}

@Composable
private fun ProfileActionItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = FitPulseTheme.colors.textSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, style = FitPulseTypography.bodyLarge, color = FitPulseTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = FitPulseTheme.colors.textTertiary)
        }
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var email by remember { mutableStateOf(profile.email) }
    var age by remember { mutableStateOf(profile.age.toString()) }
    var height by remember { mutableStateOf(profile.heightCm.toString()) }
    var weight by remember { mutableStateOf(profile.weightKg.toString()) }
    var calorieGoal by remember { mutableStateOf(profile.dailyCalorieGoal.toString()) }
    var proteinGoal by remember { mutableStateOf(profile.dailyProteinGoal.toString()) }
    var waterGoal by remember { mutableStateOf(profile.dailyWaterGoalMl.toString()) }
    var stepsGoal by remember { mutableStateOf(profile.dailyStepsGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FitPulseTheme.colors.surface,
        title = { Text("Edit Profile", color = FitPulseTheme.colors.textPrimary) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                ProfileField(name, { name = it }, "Name", isNumber = false)
                ProfileField(email, { email = it }, "Email", isNumber = false)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileField(age, { age = it }, "Age", Modifier.weight(1f))
                    ProfileField(height, { height = it }, "Height (cm)", Modifier.weight(1f))
                }
                ProfileField(weight, { weight = it }, "Weight (kg)")

                HorizontalDivider(color = Border.copy(alpha = 0.3f))
                Text("Daily Goals", style = FitPulseTypography.titleMedium, color = Primary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileField(calorieGoal, { calorieGoal = it }, "Calories", Modifier.weight(1f))
                    ProfileField(proteinGoal, { proteinGoal = it }, "Protein (g)", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileField(waterGoal, { waterGoal = it }, "Water (ml)", Modifier.weight(1f))
                    ProfileField(stepsGoal, { stepsGoal = it }, "Steps", Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            GradientButton(text = "Save", onClick = {
                onSave(profile.copy(
                    name = name.trim(),
                    email = email.trim(),
                    age = age.toIntOrNull() ?: profile.age,
                    heightCm = height.toFloatOrNull() ?: profile.heightCm,
                    weightKg = weight.toFloatOrNull() ?: profile.weightKg,
                    dailyCalorieGoal = calorieGoal.toIntOrNull() ?: profile.dailyCalorieGoal,
                    dailyProteinGoal = proteinGoal.toIntOrNull() ?: profile.dailyProteinGoal,
                    dailyWaterGoalMl = waterGoal.toIntOrNull() ?: profile.dailyWaterGoalMl,
                    dailyStepsGoal = stepsGoal.toIntOrNull() ?: profile.dailyStepsGoal
                ))
            })
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = FitPulseTheme.colors.textTertiary) } }
    )
}

@Composable
private fun ProfileField(
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
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary, unfocusedBorderColor = Border,
            focusedTextColor = FitPulseTheme.colors.textPrimary, unfocusedTextColor = FitPulseTheme.colors.textPrimary,
            cursorColor = Primary, focusedLabelColor = Primary, unfocusedLabelColor = FitPulseTheme.colors.textTertiary
        )
    )
}

// ============================================================================
// ACHIEVEMENTS SCREEN
// ============================================================================
@Composable
fun AchievementsScreen(
    viewModel: FitPulseViewModel,
    onBack: () -> Unit
) {
    val achievements by viewModel.achievements.collectAsState()
    val unlockedCount by viewModel.unlockedAchievementCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
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
            Text("Achievements", style = FitPulseTypography.headlineLarge, color = FitPulseTheme.colors.textPrimary)
        }

        // Summary
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressRing(
                    progress = if (achievements.isNotEmpty()) unlockedCount.toFloat() / achievements.size else 0f,
                    size = 80.dp,
                    strokeWidth = 8.dp,
                    progressColor = Warning
                ) {
                    Text("Trophy", style = FitPulseTypography.titleLarge)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("$unlockedCount / ${achievements.size}", style = FitPulseTypography.statValue, color = FitPulseTheme.colors.textPrimary)
                    Text("Achievements Unlocked", style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textSecondary)
                }
            }
        }

        // Achievement Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { achievement ->
                AchievementBadge(
                    name = achievement.name,
                    description = achievement.description,
                    isUnlocked = achievement.isUnlocked,
                    progress = if (achievement.requirement > 0) achievement.currentProgress.toFloat() / achievement.requirement else 0f
                )
            }
        }
    }
}


@Composable
fun SettingsScreen(
    viewModel: FitPulseViewModel,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val reminderState by viewModel.reminderState.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val colors = FitPulseTheme.colors
    val themeMode by ThemeManager.themeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showImportBackupDialog by remember { mutableStateOf(false) }
    var isExportingBackup by remember { mutableStateOf(false) }
    var isImportingBackup by remember { mutableStateOf(false) }
    var pendingImportBackupUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isExportingBackup = true
                runCatching {
                    viewModel.exportLocalBackup(uri)
                }.onSuccess { result ->
                    android.widget.Toast.makeText(
                        context,
                        "Backup exported: ${result.workoutCount} workouts, ${result.mealCount} meals",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }.onFailure {
                    android.widget.Toast.makeText(
                        context,
                        "Backup export failed",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                isExportingBackup = false
            }
        }
    }
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            pendingImportBackupUri = uri
            showImportBackupDialog = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loadReminderState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Text("Settings", style = FitPulseTypography.headlineLarge, color = colors.textPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ========== APPEARANCE ==========
        SectionHeader(title = "Appearance")

        SettingsItem(
            icon = Icons.Default.Palette, 
            title = "Theme", 
            subtitle = themeMode.displayName,
            colors = colors
        ) { 
            showThemeDialog = true
        }

        // ========== GENERAL ==========
        SectionHeader(title = "General")
        
        SettingsItem(
            icon = Icons.Default.Notifications, 
            title = "Workout Reminders", 
            subtitle = if (reminderState.isEnabled) "Daily at ${String.format("%02d:%02d", reminderState.hour, reminderState.minute)}" else "Disabled",
            colors = colors
        ) {
             val timePickerDialog = android.app.TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    viewModel.setWorkoutReminder(hourOfDay, minute)
                    android.widget.Toast.makeText(context, "Reminder set!", android.widget.Toast.LENGTH_SHORT).show()
                },
                reminderState.hour,
                reminderState.minute,
                true
            )
            timePickerDialog.setButton(android.content.DialogInterface.BUTTON_NEUTRAL, "Turn Off") { _, _ ->
                viewModel.cancelWorkoutReminder()
                android.widget.Toast.makeText(context, "Reminder disabled", android.widget.Toast.LENGTH_SHORT).show()
            }
            timePickerDialog.show()
        }

        // Water Reminders Toggle
        SettingsToggleItem(
            icon = Icons.Default.WaterDrop,
            title = "Water Reminders",
            subtitle = if (reminderState.waterRemindersEnabled) "Every 2 hours" else "Disabled",
            isChecked = reminderState.waterRemindersEnabled,
            colors = colors
        ) { enabled ->
            viewModel.setWaterReminders(enabled)
            android.widget.Toast.makeText(
                context,
                if (enabled) "Water reminders enabled" else "Water reminders disabled",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        // Unit System
        SettingsItem(
            icon = Icons.Default.Language,
            title = "Units",
            subtitle = if (profile?.unitSystem == UnitSystem.IMPERIAL) "Imperial (lbs, in)" else "Metric (kg, cm)",
            colors = colors
        ) {
            showUnitDialog = true
        }        // ========== DATA ==========
        SectionHeader(title = "Data")

        SettingsInfoItem(
            Icons.Default.PhoneAndroid,
            "Offline-first storage",
            "Workouts, nutrition, progress, and saved guides stay on this device in the current build.",
            colors
        )
        SettingsInfoItem(
            Icons.Default.PrivacyTip,
            "Privacy",
            "There is no social feed or cloud-sharing flow in the main app path right now.",
            colors
        )
        SettingsInfoItem(
            Icons.Default.SaveAs,
            "Session recovery",
            "In-progress workouts are auto-saved locally so you can reopen the app and continue the session.",
            colors
        )
        SettingsItem(
            Icons.Default.Download,
            "Export Local Backup",
            if (isExportingBackup) "Creating backup JSON..." else "Save workouts, nutrition, progress, and your current session to a file",
            colors
        ) {
            exportBackupLauncher.launch("fitpulse-backup-${Utils.getTodayString()}.json")
        }
        SettingsItem(
            Icons.Default.UploadFile,
            "Import Local Backup",
            if (isImportingBackup) "Restoring local data..." else "Replace local data from a FitPulse backup JSON after validation",
            colors
        ) {
            importBackupLauncher.launch(arrayOf("application/json", "text/plain"))
        }
        SettingsInfoItem(
            Icons.Default.AdminPanelSettings,
            "Safer restore path",
            "Backups are validated before restore, and local database rows are replaced in one restore pass to reduce partial-import risk.",
            colors
        )
        SettingsItem(Icons.Default.DeleteSweep, "Clear All Data", "Warning: irreversible", colors) {
            showClearDataDialog = true
        }

        // ========== ABOUT ==========
        SectionHeader(title = "About")

        SettingsInfoItem(Icons.Default.Info, "Version", "2.0.0 (Build 3)", colors)
        SettingsInfoItem(Icons.Default.Code, "Developer", "Mr.CrAzY", colors)
        SettingsInfoItem(Icons.Default.Email, "Support", "fitpulse@support.com", colors)

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("FitPulse Pro", style = FitPulseTypography.titleLarge, color = colors.textTertiary)
            Text("Made for fitness enthusiasts", style = FitPulseTypography.bodySmall, color = colors.textTertiary)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // ========== THEME PICKER DIALOG ==========
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = colors.surfaceElevated,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary,
            title = {
                Text("Choose Theme", style = FitPulseTypography.headlineSmall)
            },
            text = {
                Column {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = mode == themeMode
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    ThemeManager.setTheme(context, mode)
                                    showThemeDialog = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else colors.card.copy(alpha = 0.5f)
                            ),
                            border = if (isSelected)
                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                            else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (mode) {
                                            ThemeMode.SYSTEM -> Icons.Default.SettingsBrightness
                                            ThemeMode.DARK -> Icons.Default.DarkMode
                                            ThemeMode.LIGHT -> Icons.Default.LightMode
                                            ThemeMode.AMOLED -> Icons.Default.Contrast
                                            ThemeMode.MIDNIGHT -> Icons.Default.Nightlight
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else colors.textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            mode.displayName,
                                            style = FitPulseTypography.bodyLarge,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else colors.textPrimary,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            mode.description,
                                            style = FitPulseTypography.bodySmall,
                                            color = colors.textTertiary
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Close", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // ========== UNIT SYSTEM DIALOG ==========
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            containerColor = colors.surfaceElevated,
            title = { Text("Unit System", style = FitPulseTypography.headlineSmall, color = colors.textPrimary) },
            text = {
                Column {
                    listOf(UnitSystem.METRIC to "Metric (kg, cm)", UnitSystem.IMPERIAL to "Imperial (lbs, in)").forEach { (unit, label) ->
                        val isSelected = profile?.unitSystem == unit
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    profile?.let { viewModel.saveProfile(it.copy(unitSystem = unit)) }
                                    showUnitDialog = false
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Primary.copy(alpha = 0.15f) else colors.card.copy(alpha = 0.5f)
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, Primary) else null
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, style = FitPulseTypography.bodyLarge, color = if (isSelected) Primary else colors.textPrimary)
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(22.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text("Close", color = Primary)
                }
            }
        )
    }

    // ========== CLEAR DATA CONFIRMATION ==========
    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            containerColor = colors.surfaceElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Error, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Clear All Data?", style = FitPulseTypography.headlineSmall, color = colors.textPrimary)
                }
            },
            text = {
                Text(
                    "This will permanently delete all your workouts, meals, measurements, progress photos, achievements, and personal records. This action cannot be undone.",
                    style = FitPulseTypography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                        android.widget.Toast.makeText(context, "All data cleared", android.widget.Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error)
                ) {
                    Text("Delete Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }

    if (showImportBackupDialog && pendingImportBackupUri != null) {
        AlertDialog(
            onDismissRequest = {
                showImportBackupDialog = false
                pendingImportBackupUri = null
            },
            containerColor = colors.surfaceElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = Warning, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Import Backup?", style = FitPulseTypography.headlineSmall, color = colors.textPrimary)
                }
            },
            text = {
                Text(
                    "This replaces the current local workouts, nutrition, progress, saved articles, reminders, and any in-progress workout with the selected backup.",
                    style = FitPulseTypography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val backupUri = pendingImportBackupUri ?: return@Button
                        scope.launch {
                            isImportingBackup = true
                            runCatching {
                                viewModel.importLocalBackup(backupUri)
                            }.onSuccess { result ->
                                android.widget.Toast.makeText(
                                    context,
                                    "Backup restored: ${result.workoutCount} workouts, ${result.measurementCount} measurements",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }.onFailure {
                                android.widget.Toast.makeText(
                                    context,
                                    "Backup import failed",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                            isImportingBackup = false
                            showImportBackupDialog = false
                            pendingImportBackupUri = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Warning)
                ) {
                    Text("Import Backup", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImportBackupDialog = false
                        pendingImportBackupUri = null
                    }
                ) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: FitPulseColors = FitPulseTheme.colors,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 3.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = FitPulseTypography.bodyLarge, color = colors.textPrimary)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = FitPulseTypography.bodySmall, color = colors.textSecondary)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = colors.textTertiary)
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: FitPulseColors = FitPulseTheme.colors
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Border.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = FitPulseTypography.bodyLarge, color = colors.textPrimary)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, style = FitPulseTypography.bodySmall, color = colors.textTertiary)
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    colors: FitPulseColors = FitPulseTheme.colors,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 3.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = FitPulseTypography.bodyLarge, color = colors.textPrimary)
                if (subtitle.isNotEmpty()) {
                    Text(subtitle, style = FitPulseTypography.bodySmall, color = colors.textSecondary)
                }
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = colors.textTertiary,
                    uncheckedTrackColor = colors.card
                )
            )
        }
    }
}








