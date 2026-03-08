package com.fitpulse.pro.ui.screens.social

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.EmptyState
import com.fitpulse.pro.ui.components.GlassCard
import com.fitpulse.pro.ui.components.GradientButton
import com.fitpulse.pro.ui.components.FitPulseChip
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.utils.HapticHelper
import com.fitpulse.pro.utils.Utils
import com.fitpulse.pro.viewmodel.FitPulseViewModel

@Composable
fun SocialHubScreen(
    viewModel: FitPulseViewModel,
    onShareWorkout: ((Long) -> Unit)? = null
) {
    val userXP by viewModel.totalXP.collectAsState()
    val userLevel by viewModel.currentLevel.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    val activeChallenges by viewModel.activeChallenges.collectAsState()
    
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .testTag(TestTags.SocialScreen)
    ) {
        // TOP HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Column {
                Text(
                    text = "Social Hub",
                    style = FitPulseTypography.displaySmall,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Compete and connect with athletes",
                    style = FitPulseTypography.bodyMedium,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
            
            // PRO Badge
            Surface(
                color = Primary,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = "PRO",
                    style = FitPulseTypography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Your Stats Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(GradientStart.copy(alpha = 0.2f), GradientEnd.copy(alpha = 0.15f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (profile?.name?.firstOrNull() ?: "Y").toString().uppercase(),
                            color = Primary,
                            fontWeight = FontWeight.Bold,
                            style = FitPulseTypography.titleLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile?.name?.ifBlank { "You" } ?: "You",
                            style = FitPulseTypography.titleMedium,
                            color = FitPulseTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Level $userLevel • ${viewModel.xpManager.getRank()}",
                            style = FitPulseTypography.bodySmall,
                            color = FitPulseTheme.colors.textSecondary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$userXP",
                            style = FitPulseTypography.headlineMedium,
                            color = Primary,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Total XP",
                            style = FitPulseTypography.labelSmall,
                            color = FitPulseTheme.colors.textTertiary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = Primary,
            divider = { HorizontalDivider(color = Border.copy(alpha = 0.3f)) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { 
                    Text(
                        "Leaderboard",
                        color = if (selectedTab == 0) Primary else FitPulseTheme.colors.textTertiary
                    ) 
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { 
                    Text(
                        "Feed",
                        color = if (selectedTab == 1) Primary else FitPulseTheme.colors.textTertiary
                    ) 
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { 
                    Text(
                        "Groups",
                        color = if (selectedTab == 2) Primary else FitPulseTheme.colors.textTertiary
                    ) 
                }
            )
        }

        // Content
        when (selectedTab) {
            0 -> LeaderboardTab(
                userName = profile?.name?.ifBlank { "You" } ?: "You",
                userXP = userXP,
                userLevel = userLevel
            )
            1 -> FeedTab(
                workouts = recentWorkouts,
                userName = profile?.name?.ifBlank { "You" } ?: "You",
                onShareWorkout = onShareWorkout
            )
            2 -> GroupsTab(
                challenges = activeChallenges,
                onCreateChallenge = { challenge ->
                    viewModel.createChallenge(challenge)
                }
            )
        }
    }
}

@Composable
private fun LeaderboardTab(userName: String, userXP: Int, userLevel: Int) {
    // Build dynamic leaderboard with user's real XP
    val competitors = listOf(
        LeaderboardEntry(0, "Alex Rivers", "Lifting Enthusiast", 12450),
        LeaderboardEntry(0, "Sarah Chen", "Marathon Runner", 11800),
        LeaderboardEntry(0, "Mike Johnson", "Hybrid Athlete", 10900),
        LeaderboardEntry(0, "Emma Wilson", "Yoga Pro", 9500),
        LeaderboardEntry(0, "David Smith", "Casual Gym Goer", 8200),
        LeaderboardEntry(0, "Zoe Martinez", "CrossFit Lover", 6100),
        LeaderboardEntry(0, "Jake Thompson", "Beginner", 3500)
    )
    
    // Merge user into list and sort by XP
    val allEntries = (competitors + LeaderboardEntry(0, userName, "Level $userLevel", userXP, isUser = true))
        .sortedByDescending { it.points }
        .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Top Performers (This Week)",
                style = FitPulseTypography.titleLarge,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(allEntries) { entry ->
            LeaderboardItem(entry)
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FeedTab(
    workouts: List<Workout>,
    userName: String,
    onShareWorkout: ((Long) -> Unit)?
) {
    if (workouts.isEmpty()) {
        EmptyState(
            icon = Icons.Default.DynamicFeed,
            title = "No Activity Yet",
            subtitle = "Complete a workout to see it in your feed"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Recent Activity",
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(workouts.take(15)) { workout ->
                FeedCard(
                    workout = workout,
                    userName = userName,
                    onShareWorkout = onShareWorkout
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FeedCard(
    workout: Workout,
    userName: String,
    onShareWorkout: ((Long) -> Unit)?
) {
    val view = LocalView.current
    val safeExercises = workout.exercises.orEmpty()

    // Mock reaction state (local only)
    var heartCount by remember { mutableIntStateOf((workout.totalCalories % 7) + 2) }
    var fireCount by remember { mutableIntStateOf((workout.totalVolume.toInt() % 5) + 1) }
    var isHearted by remember { mutableStateOf(false) }
    var isFired by remember { mutableStateOf(false) }

    // Heart animation
    val heartScale by animateFloatAsState(
        targetValue = if (isHearted) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heart_scale"
    )
    val fireScale by animateFloatAsState(
        targetValue = if (isFired) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fire_scale"
    )

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        // User Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Utils.formatDate(workout.createdAt),
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textTertiary
                )
            }
            // Mood emoji
            workout.mood?.let { mood ->
                Text(
                    text = when (mood) {
                        WorkoutMood.GREAT -> "🔥"
                        WorkoutMood.GOOD -> "💪"
                        WorkoutMood.OKAY -> "👍"
                        WorkoutMood.TIRED -> "😮‍💨"
                        WorkoutMood.TERRIBLE -> "😵"
                    },
                    style = FitPulseTypography.headlineSmall
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Workout Info
        Text(
            text = "Completed: ${workout.name}",
            style = FitPulseTypography.titleLarge,
            color = FitPulseTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )

        // Auto-generated comparison line
        val comparison = remember(workout.id) {
            val variants = listOf(
                "💥 Crushed it with ${(workout.exercises ?: emptyList()).size} exercises!",
                "📈 ${workout.totalCalories} calories torched in ${workout.durationMinutes} min",
                "🏋️ ${Utils.formatWeight(workout.totalVolume)} total volume — beast mode!",
                "⚡ ${workout.durationMinutes} min of pure effort"
            )
            variants[(workout.id % variants.size).toInt()]
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comparison,
            style = FitPulseTypography.bodyMedium,
            color = Accent,
            fontWeight = FontWeight.Medium
        )
        
        val notes = workout.notes
        if (!notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "\"$notes\"",
                style = FitPulseTypography.bodyMedium,
                color = FitPulseTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeedStat(label = "Duration", value = Utils.formatDuration(workout.durationMinutes), color = Primary)
            FeedStat(label = "Exercises", value = "${safeExercises.size}", color = Secondary)
            FeedStat(label = "Volume", value = Utils.formatWeight(workout.totalVolume), color = Accent)
            FeedStat(label = "Calories", value = "${workout.totalCalories}", color = Warning)
        }
        
        // Rating stars
        if (workout.rating > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                (1..5).forEach { star ->
                    Icon(
                        if (star <= workout.rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (star <= workout.rating) Warning else FitPulseTheme.colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Exercise list preview
        if (safeExercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Border.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))
            safeExercises.take(3).forEach { ex ->
                val safeSets = ex.sets ?: emptyList()
                val completedSets = safeSets.count { it.isCompleted }
                Text(
                    text = "✅ ${ex.exerciseName} — ${completedSets}/${safeSets.size} sets",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
            if (safeExercises.size > 3) {
                Text(
                    text = "+${safeExercises.size - 3} more exercises",
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textTertiary
                )
            }
        }

        // ===== Reactions Row =====
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Border.copy(alpha = 0.15f))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Heart reaction
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        HapticHelper.tick(view)
                        isHearted = !isHearted
                        heartCount += if (isHearted) 1 else -1
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    if (isHearted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isHearted) Error else FitPulseTheme.colors.textTertiary,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer(scaleX = heartScale, scaleY = heartScale)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "$heartCount",
                    style = FitPulseTypography.labelMedium,
                    color = if (isHearted) Error else FitPulseTheme.colors.textTertiary
                )
            }

            // Fire reaction
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        HapticHelper.tick(view)
                        isFired = !isFired
                        fireCount += if (isFired) 1 else -1
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "🔥",
                    modifier = Modifier.graphicsLayer(scaleX = fireScale, scaleY = fireScale)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "$fireCount",
                    style = FitPulseTypography.labelMedium,
                    color = if (isFired) Warning else FitPulseTheme.colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Share
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                tint = FitPulseTheme.colors.textTertiary,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .clickable {
                        HapticHelper.tick(view)
                        onShareWorkout?.invoke(workout.id)
                    }
            )
        }
    }
}

@Composable
private fun FeedStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = FitPulseTypography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = FitPulseTypography.labelSmall,
            color = FitPulseTheme.colors.textTertiary
        )
    }
}

@Composable
private fun GroupsTab(
    challenges: List<Challenge>,
    onCreateChallenge: (Challenge) -> Unit
) {
    if (challenges.isEmpty()) {
        EmptyState(
            icon = Icons.Default.Group,
            title = "No Active Challenges",
            subtitle = "Create a challenge to compete with friends",
            actionText = "Create Challenge",
            onAction = {
                onCreateChallenge(
                    Challenge(
                        name = "Weekly Workout Challenge",
                        description = "Complete 5 workouts this week",
                        type = ChallengeType.WORKOUTS,
                        target = 5,
                        endDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                        xpReward = 500
                    )
                )
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Active Challenges",
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(challenges) { challenge ->
                ChallengeCard(challenge = challenge)
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                GradientButton(
                    text = "Create New Challenge",
                    onClick = {
                        onCreateChallenge(
                            Challenge(
                                name = "Daily Step Challenge",
                                description = "Walk 10,000 steps every day for a week",
                                type = ChallengeType.STEPS,
                                target = 70000,
                                endDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                                xpReward = 750
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Default.Add,
                    colors = listOf(Secondary.copy(alpha = 0.8f), GradientCyan.copy(alpha = 0.8f))
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ChallengeCard(challenge: Challenge) {
    val progress = if (challenge.target > 0) 
        (challenge.currentProgress.toFloat() / challenge.target).coerceIn(0f, 1f) 
    else 0f
    
    val daysLeft = ((challenge.endDate - System.currentTimeMillis()) / (24 * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
    
    val typeEmoji = when (challenge.type) {
        ChallengeType.STEPS -> "🚶"
        ChallengeType.WORKOUTS -> "🏋️"
        ChallengeType.CALORIES_BURNED -> "🔥"
        ChallengeType.VOLUME_LIFTED -> "💪"
        ChallengeType.ACTIVE_MINUTES -> "⏱️"
        ChallengeType.WATER_INTAKE -> "💧"
        ChallengeType.STREAK -> "🔥"
    }
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = typeEmoji,
                style = FitPulseTypography.headlineLarge,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.name,
                    style = FitPulseTypography.titleMedium,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = challenge.description,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Progress bar
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(800),
                    label = "challenge_progress"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Border.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Primary, GradientEnd)
                                )
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${challenge.currentProgress} / ${challenge.target}",
                        style = FitPulseTypography.labelSmall,
                        color = FitPulseTheme.colors.textTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$daysLeft days left",
                            style = FitPulseTypography.labelSmall,
                            color = if (daysLeft <= 2) Warning else FitPulseTheme.colors.textTertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        FitPulseChip(
                            text = "⚡ ${challenge.xpReward} XP",
                            selected = true,
                            color = Success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(entry: LeaderboardEntry) {
    val isTopThree = entry.rank <= 3
    val isUser = entry.isUser
    
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank with medal for top 3
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isTopThree) {
                    Text(
                        text = when (entry.rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#${entry.rank}"
                        },
                        style = FitPulseTypography.titleLarge
                    )
                } else {
                    Text(
                        text = "#${entry.rank}",
                        style = FitPulseTypography.titleMedium,
                        color = FitPulseTheme.colors.textTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUser) Primary.copy(alpha = 0.2f)
                        else FitPulseTheme.colors.surfaceElevated
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.name.take(1),
                    color = if (isUser) Primary else FitPulseTheme.colors.textSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.name,
                        style = FitPulseTypography.titleMedium,
                        color = if (isUser) Primary else FitPulseTheme.colors.textPrimary,
                        fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "YOU",
                                style = FitPulseTypography.labelSmall,
                                color = Primary,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(
                    text = entry.status,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Utils.formatNumber(entry.points),
                    style = FitPulseTypography.titleLarge,
                    color = if (isUser) Primary else if (isTopThree) Warning else FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "XP",
                    style = FitPulseTypography.labelSmall,
                    color = FitPulseTheme.colors.textTertiary
                )
            }
        }
    }
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val status: String,
    val points: Int,
    val isUser: Boolean = false
)
