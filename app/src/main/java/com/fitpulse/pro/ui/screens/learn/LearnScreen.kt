package com.fitpulse.pro.ui.screens.learn

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fitpulse.pro.data.model.*
import com.fitpulse.pro.ui.TestTags
import com.fitpulse.pro.ui.components.*
import com.fitpulse.pro.ui.theme.*
import com.fitpulse.pro.viewmodel.FitPulseViewModel

@Composable
fun LearnScreen(
    viewModel: FitPulseViewModel,
    onNavigateToExerciseLibrary: (String?) -> Unit,
    onNavigateToArticle: (String) -> Unit
) {
    val exercises by viewModel.allExercises.collectAsState()
    val savedArticleIds by viewModel.savedArticleIds.collectAsState()
    val lastReadArticleId by viewModel.lastReadArticleId.collectAsState()
    val articles = viewModel.articles

    var selectedArticleCategory by remember { mutableStateOf<ArticleCategory?>(null) }
    var selectedKnowledgeLevel by remember { mutableStateOf<KnowledgeLevel?>(null) }
    var articleSearchQuery by remember { mutableStateOf("") }
    var showSavedOnly by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val compactSearchField = configuration.screenWidthDp < 360 || configuration.fontScale > 1.15f
    val searchPlaceholder = if (compactSearchField) {
        "Search guides and myths"
    } else {
        "Search guides, myths, nutrition, recovery..."
    }
    val availableCategories = remember(articles) { articles.map { it.category }.distinct() }
    val exerciseCountsByMuscleGroup = remember(exercises) {
        exercises.groupingBy { it.muscleGroup }.eachCount()
    }
    val knowledgeTracks = remember(articles) { buildKnowledgeTracks(articles) }
    val startHereTrack = remember(knowledgeTracks) {
        knowledgeTracks.firstOrNull { it.id == "start_here" }
    }
    val continueReadingArticle = remember(articles, lastReadArticleId) {
        articles.firstOrNull { it.id == lastReadArticleId }
    }
    val articlePriority = remember(articles) {
        recommendedKnowledgeArticleOrder(articles)
            .withIndex()
            .associate { it.value to it.index }
    }
    val filteredArticles = remember(
        articles,
        selectedArticleCategory,
        selectedKnowledgeLevel,
        articleSearchQuery,
        articlePriority,
        showSavedOnly,
        savedArticleIds
    ) {
        filterKnowledgeArticles(
            articles = articles,
            selectedCategory = selectedArticleCategory,
            selectedKnowledgeLevel = selectedKnowledgeLevel,
            articleSearchQuery = articleSearchQuery,
            showSavedOnly = showSavedOnly,
            savedArticleIds = savedArticleIds,
            articlePriority = articlePriority
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FitPulseTheme.colors.background)
            .statusBarsPadding()
            .testTag(TestTags.LearnScreen),
        contentPadding = PaddingValues(bottom = FitPulseLayout.ScreenBottomPadding)
    ) {
        item {
            Text(
                text = "Knowledge Library",
                style = FitPulseTypography.displayMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    start = FitPulseLayout.ScreenHorizontalPadding,
                    top = FitPulseLayout.ScreenHeaderTopPadding,
                    end = FitPulseLayout.ScreenHorizontalPadding,
                    bottom = 8.dp
                )
            )
            Text(
                text = "Beginner-to-expert fitness paths, evidence-backed guides, and the 90% of advice that matters most.",
                style = FitPulseTypography.bodyMedium,
                color = FitPulseTheme.colors.textSecondary,
                modifier = Modifier.padding(
                    start = FitPulseLayout.ScreenHorizontalPadding,
                    end = FitPulseLayout.ScreenHorizontalPadding,
                    bottom = 16.dp
                )
            )
        }

        item {
            OutlinedTextField(
                value = articleSearchQuery,
                onValueChange = { articleSearchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding),
                placeholder = {
                    Text(
                        text = searchPlaceholder,
                        color = FitPulseTheme.colors.textTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = FitPulseTheme.colors.textTertiary
                    )
                },
                trailingIcon = {
                    if (articleSearchQuery.isNotBlank()) {
                        IconButton(onClick = { articleSearchQuery = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = FitPulseTheme.colors.textTertiary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    focusedTextColor = FitPulseTheme.colors.textPrimary,
                    unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                    cursorColor = Primary
                )
            )
        }

        if (startHereTrack != null) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Start Here")
                StartHereJourneyCard(
                    track = startHereTrack,
                    onClick = { onNavigateToArticle(startHereTrack.leadArticle.id) }
                )
            }
        }

        if (continueReadingArticle != null) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionHeader(title = "Continue Reading")
                ContinueReadingCard(
                    article = continueReadingArticle,
                    isSaved = continueReadingArticle.id in savedArticleIds,
                    onClick = { onNavigateToArticle(continueReadingArticle.id) }
                )
            }
        }

        if (knowledgeTracks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Learning Paths")
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = knowledgeTracks.filterNot { it.id == "start_here" },
                        key = { it.id }
                    ) { track ->
                        KnowledgeTrackCard(
                            track = track,
                            onClick = { onNavigateToArticle(track.leadArticle.id) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Exercise Reference")
            ExerciseReferenceSection(
                exerciseCount = exercises.size,
                onNavigateToExerciseLibrary = onNavigateToExerciseLibrary,
                onNavigateToArticle = onNavigateToArticle
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Explore by Muscle Group")

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val muscleGroups = listOf(
                    Triple("Chest", MuscleGroup.CHEST, Primary),
                    Triple("Back", MuscleGroup.BACK, Secondary),
                    Triple("Shoulders", MuscleGroup.SHOULDERS, Accent),
                    Triple("Arms", MuscleGroup.BICEPS, Warning),
                    Triple("Legs", MuscleGroup.QUADRICEPS, Success),
                    Triple("Core", MuscleGroup.ABS, ChartPink),
                    Triple("Glutes", MuscleGroup.GLUTES, GradientMiddle),
                    Triple("Cardio", MuscleGroup.CARDIO_SYSTEM, Info)
                )

                items(items = muscleGroups, key = { it.second.name }) { (name, group, color) ->
                    MuscleGroupCard(
                        name = name,
                        exerciseCount = exerciseCountsByMuscleGroup[group] ?: 0,
                        color = color,
                        onClick = { onNavigateToExerciseLibrary(group.name) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Knowledge Library")
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FitPulseChip(
                        text = "All",
                        selected = selectedArticleCategory == null,
                        onClick = { selectedArticleCategory = null }
                    )
                }
                items(items = availableCategories, key = { it.name }) { category ->
                    FitPulseChip(
                        text = category.displayName(),
                        selected = selectedArticleCategory == category,
                        color = category.accentColor(),
                        onClick = { selectedArticleCategory = if (selectedArticleCategory == category) null else category }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FitPulseChip(
                        text = "Saved",
                        selected = showSavedOnly,
                        color = Warning,
                        onClick = { showSavedOnly = !showSavedOnly }
                    )
                }
                item {
                    FitPulseChip(
                        text = "All Entry Levels",
                        selected = selectedKnowledgeLevel == null,
                        color = Secondary,
                        onClick = { selectedKnowledgeLevel = null }
                    )
                }
                items(items = KnowledgeLevel.entries.toList(), key = { it.name }) { level ->
                    FitPulseChip(
                        text = level.displayName(),
                        selected = selectedKnowledgeLevel == level,
                        color = level.accentColor(),
                        onClick = {
                            selectedKnowledgeLevel = if (selectedKnowledgeLevel == level) null else level
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (articleSearchQuery.isNotBlank() || selectedArticleCategory != null || selectedKnowledgeLevel != null) {
            item {
                Text(
                    text = "${filteredArticles.size} guide${if (filteredArticles.size == 1) "" else "s"} found",
                    style = FitPulseTypography.labelLarge,
                    color = FitPulseTheme.colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }

        if (filteredArticles.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    title = "No guides match the current filters",
                    subtitle = "Try a broader search or reset the selected category and level.",
                    actionText = "Reset",
                    onAction = {
                        articleSearchQuery = ""
                        selectedArticleCategory = null
                        selectedKnowledgeLevel = null
                        showSavedOnly = false
                    }
                )
            }
        } else {
            items(filteredArticles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    isSaved = article.id in savedArticleIds,
                    onClick = { onNavigateToArticle(article.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "Quick Tips")

            val tips = listOf(
                "Always warm up for 5-10 minutes before lifting",
                "Total daily protein matters more than perfect meal timing",
                "Progressive overload can come from reps, sets, or better range - not only more weight",
                "Recovery is where training adaptations actually stick",
                "Use HIIT sparingly if strength is your top priority",
                "Track your workouts to ensure consistent progress",
                "Mobility works best when you strengthen the new range you earn",
                "Creatine monohydrate remains the evidence-first supplement for most lifters"
            )

            tips.forEachIndexed { index, tip ->
                TipCard(
                    number = index + 1,
                    tip = tip,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ExerciseReferenceSection(
    exerciseCount: Int,
    onNavigateToExerciseLibrary: (String?) -> Unit,
    onNavigateToArticle: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FitPulseLayout.ScreenHorizontalPadding)
    ) {
        val compactLayout = maxWidth < FitPulseLayout.MediumScreenBreakpoint

        if (compactLayout) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(FitPulseLayout.CardSpacing)) {
                item {
                    LearnQuickCard(
                        title = "Exercise\nLibrary",
                        subtitle = "$exerciseCount exercises",
                        icon = Icons.Default.FitnessCenter,
                        color = Primary,
                        modifier = Modifier.width(148.dp),
                        onClick = { onNavigateToExerciseLibrary(null) }
                    )
                }
                item {
                    LearnQuickCard(
                        title = "Muscle\nGroups",
                        subtitle = "Body-region guide",
                        icon = Icons.Default.Accessibility,
                        color = Accent,
                        modifier = Modifier.width(148.dp),
                        onClick = { onNavigateToExerciseLibrary(null) }
                    )
                }
                item {
                    LearnQuickCard(
                        title = "Joint-\nFriendly",
                        subtitle = "Warm-up guide",
                        icon = Icons.Default.SelfImprovement,
                        color = Warning,
                        modifier = Modifier.width(148.dp),
                        onClick = { onNavigateToArticle("warm_up_and_injury_prevention") }
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FitPulseLayout.CardSpacing)
            ) {
                LearnQuickCard(
                    title = "Exercise\nLibrary",
                    subtitle = "$exerciseCount exercises",
                    icon = Icons.Default.FitnessCenter,
                    color = Primary,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToExerciseLibrary(null) }
                )
                LearnQuickCard(
                    title = "Muscle\nGroups",
                    subtitle = "Body-region guide",
                    icon = Icons.Default.Accessibility,
                    color = Accent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToExerciseLibrary(null) }
                )
                LearnQuickCard(
                    title = "Joint-\nFriendly",
                    subtitle = "Warm-up guide",
                    icon = Icons.Default.SelfImprovement,
                    color = Warning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToArticle("warm_up_and_injury_prevention") }
                )
            }
        }
    }
}

@Composable
private fun LearnQuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = FitPulseTypography.titleMedium,
                color = FitPulseTheme.colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                lineHeight = FitPulseTypography.titleMedium.lineHeight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = FitPulseTypography.bodySmall,
                color = FitPulseTheme.colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StartHereJourneyCard(
    track: KnowledgeTrack,
    onClick: () -> Unit
) {
    GradientCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = listOf(GradientStart, GradientEnd),
        onClick = onClick
    ) {
        Text(
            text = track.title,
            style = FitPulseTypography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = track.description,
            style = FitPulseTypography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TrackMetaPill(text = "${track.articles.size} guides")
            TrackMetaPill(text = "${track.totalReadTimeMinutes} min")
            TrackMetaPill(text = "Beginner first")
        }
        Spacer(modifier = Modifier.height(14.dp))
        track.articles.forEachIndexed { index, article ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = FitPulseTypography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = article.title,
                    style = FitPulseTypography.bodyMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Open ${track.leadArticle.title}",
            style = FitPulseTypography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ContinueReadingCard(
    article: FitnessArticle,
    isSaved: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.quickTakeaway,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrackMetaPill(text = "${article.readTimeMinutes} min")
                    if (isSaved) {
                        TrackMetaPill(text = "Saved")
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = FitPulseTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun TrackMetaPill(text: String) {
    Surface(
        color = Color.White.copy(alpha = 0.14f),
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            style = FitPulseTypography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun KnowledgeTrackCard(
    track: KnowledgeTrack,
    onClick: () -> Unit
) {
    val accentColor = track.accentColor()
    GlassCard(
        modifier = Modifier.width(240.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = track.icon(),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = track.subtitle,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = track.description,
            style = FitPulseTypography.bodyMedium,
            color = FitPulseTheme.colors.textSecondary,
            minLines = 2
        )
        Spacer(modifier = Modifier.height(12.dp))
        track.articles.take(2).forEach { article ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = article.title,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "${track.articles.size} guides | ${track.totalReadTimeMinutes} min total",
            style = FitPulseTypography.labelMedium,
            color = accentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MuscleGroupCard(
    name: String,
    exerciseCount: Int,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
                    )
                )
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, style = FitPulseTypography.titleLarge, color = FitPulseTheme.colors.textPrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("$exerciseCount exercises", style = FitPulseTypography.labelMedium, color = FitPulseTheme.colors.textSecondary)
            }
        }
    }
}

private fun KnowledgeTrack.icon(): ImageVector = when (id) {
    "start_here" -> Icons.Default.Flag
    "fitness_fundamentals" -> Icons.AutoMirrored.Filled.MenuBook
    "strength_training_basics" -> Icons.Default.FitnessCenter
    "muscle_gain_basics" -> Icons.Default.FitnessCenter
    "fat_loss_basics" -> Icons.Default.Lightbulb
    "nutrition_basics" -> Icons.Default.Restaurant
    "recovery_basics" -> Icons.Default.Bedtime
    "programming_basics" -> Icons.AutoMirrored.Filled.MenuBook
    "fitness_dos_and_donts" -> Icons.Default.Flag
    "common_fitness_myths" -> Icons.Default.Lightbulb
    else -> Icons.AutoMirrored.Filled.MenuBook
}

private fun KnowledgeTrack.accentColor(): Color = when (id) {
    "start_here" -> Primary
    "fitness_fundamentals" -> Primary
    "strength_training_basics" -> Secondary
    "muscle_gain_basics" -> GradientMiddle
    "fat_loss_basics" -> Warning
    "nutrition_basics" -> Accent
    "recovery_basics" -> Success
    "programming_basics" -> Info
    "fitness_dos_and_donts" -> ChartPink
    "common_fitness_myths" -> Secondary
    else -> Primary
}

@Composable
private fun ArticleCard(
    article: FitnessArticle,
    isSaved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = article.category.accentColor()
    val icon = article.category.icon()
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(categoryColor.copy(alpha = 0.3f), categoryColor.copy(alpha = 0.1f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = categoryColor, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    article.title,
                    style = FitPulseTypography.titleLarge,
                    color = FitPulseTheme.colors.textPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    article.summary,
                    style = FitPulseTypography.bodySmall,
                    color = FitPulseTheme.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FitPulseChip(
                            text = article.category.displayName(),
                            selected = true,
                            color = categoryColor
                        )
                    }
                    item {
                        FitPulseChip(
                            text = article.primaryLevel.displayName(),
                            selected = true,
                            color = article.primaryLevel.accentColor()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Updated ${article.lastUpdated} | ${article.readTimeMinutes} min read",
                    style = FitPulseTypography.labelSmall,
                    color = FitPulseTheme.colors.textTertiary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSaved) Warning else FitPulseTheme.colors.textTertiary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = FitPulseTheme.colors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun TipCard(
    number: Int,
    tip: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FitPulseTheme.colors.card.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("$number", style = FitPulseTypography.labelLarge, color = Primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(tip, style = FitPulseTypography.bodyMedium, color = FitPulseTheme.colors.textPrimary, modifier = Modifier.weight(1f))
        }
    }
}

// ============================================================================
// ARTICLE DETAIL SCREEN
// ============================================================================
@Composable
fun ArticleDetailScreen(
    articleId: String,
    viewModel: FitPulseViewModel,
    onNavigateToArticle: (String) -> Unit,
    onBack: () -> Unit
) {
    val article = viewModel.getArticleById(articleId)
    val savedArticleIds by viewModel.savedArticleIds.collectAsState()
    LaunchedEffect(articleId) {
        viewModel.markArticleRead(articleId)
    }
    if (article == null) {
        EmptyState(
            icon = Icons.Default.Error,
            title = "Article Not Found",
            subtitle = "This article could not be loaded",
            actionText = "Go Back",
            onAction = onBack
        )
        return
    }
    val relatedArticles = remember(article.id) {
        relatedKnowledgeArticles(
            article = article,
            articles = viewModel.articles
        )
    }
    KnowledgeArticleDetail(
        article = article,
        isSaved = article.id in savedArticleIds,
        onToggleSaved = { viewModel.toggleSavedArticle(article.id) },
        relatedArticles = relatedArticles,
        onNavigateToArticle = onNavigateToArticle,
        onBack = onBack
    )
}

// ============================================================================
// EXERCISE LIBRARY SCREEN
// ============================================================================
@Composable
fun ExerciseLibraryScreen(
    viewModel: FitPulseViewModel,
    onBack: () -> Unit,
    initialMuscleGroup: String? = null,
    onExerciseClick: ((Exercise) -> Unit)? = null,
    onNavigateToExerciseDetail: ((Long) -> Unit)? = null
) {
    val exercises by viewModel.filteredExercises.collectAsState()
    val searchQuery by viewModel.exerciseSearchQuery.collectAsState()

    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var selectedMuscleGroup by remember { 
        mutableStateOf<MuscleGroup?>(initialMuscleGroup?.let { MuscleGroup.valueOf(it) }) 
    }
    var showCreateExerciseDialog by remember { mutableStateOf(false) }

    val displayExercises = exercises.filter { exercise ->
        val categoryMatch = selectedCategory == null || exercise.category == selectedCategory
        val muscleMatch = selectedMuscleGroup == null || exercise.muscleGroup == selectedMuscleGroup
        categoryMatch && muscleMatch
    }

    // Create Exercise Dialog
    if (showCreateExerciseDialog) {
        CreateCustomExerciseDialog(
            onDismiss = { showCreateExerciseDialog = false },
            onSave = { exercise ->
                viewModel.createCustomExercise(exercise)
                showCreateExerciseDialog = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                Text("Exercise Library", style = FitPulseTypography.headlineLarge, color = FitPulseTheme.colors.textPrimary, modifier = Modifier.weight(1f))
            }

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setExerciseSearchQuery(it) },
                placeholder = { Text("Search exercises...", color = FitPulseTheme.colors.textTertiary) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = FitPulseTheme.colors.textTertiary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    focusedTextColor = FitPulseTheme.colors.textPrimary,
                    unfocusedTextColor = FitPulseTheme.colors.textPrimary,
                    cursorColor = Primary
                ),
                singleLine = true
            )

            // Category filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                item {
                    FitPulseChip(
                        text = "All Tools",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(ExerciseCategory.values().toList()) { category ->
                    FitPulseChip(
                        text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category }
                    )
                }
            }

            // Muscle group filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                item {
                    FitPulseChip(
                        text = "All Muscles",
                        selected = selectedMuscleGroup == null,
                        color = Secondary,
                        onClick = { selectedMuscleGroup = null }
                    )
                }
                items(MuscleGroup.values().toList()) { group ->
                    FitPulseChip(
                        text = group.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        selected = selectedMuscleGroup == group,
                        color = Secondary,
                        onClick = { selectedMuscleGroup = if (selectedMuscleGroup == group) null else group }
                    )
                }
            }

            // Exercise list
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = displayExercises, key = { it.id }) { exercise ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ExerciseListItem(
                                exerciseName = if (exercise.isCustom) "${exercise.name} ✨" else exercise.name,
                                category = exercise.category.name,
                                muscleGroup = exercise.muscleGroup.name.replace("_", " "),
                                difficulty = exercise.difficulty.name,
                                onClick = { 
                                    if (onExerciseClick != null) {
                                        onExerciseClick.invoke(exercise)
                                    } else {
                                        onNavigateToExerciseDetail?.invoke(exercise.id)
                                    }
                                }
                            )
                        }
                        if (exercise.isCustom) {
                            IconButton(
                                onClick = { viewModel.deleteCustomExercise(exercise) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete custom exercise",
                                    tint = com.fitpulse.pro.ui.theme.Error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // FAB to create custom exercise
        if (onExerciseClick == null) { // Only show in library mode, not picker mode
            FloatingActionButton(
                onClick = { showCreateExerciseDialog = true },
                containerColor = Primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create custom exercise")
            }
        }
    }
}

// ============================================================================
// CREATE CUSTOM EXERCISE DIALOG
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomExerciseDialog(
    onDismiss: () -> Unit,
    onSave: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExerciseCategory.STRENGTH) }
    var selectedMuscleGroup by remember { mutableStateOf(MuscleGroup.CHEST) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.INTERMEDIATE) }
    var selectedEquipment by remember { mutableStateOf(Equipment.NONE) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedMuscle by remember { mutableStateOf(false) }
    var expandedDifficulty by remember { mutableStateOf(false) }
    var expandedEquipment by remember { mutableStateOf(false) }

    val colors = FitPulseTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Create Exercise",
                    style = FitPulseTypography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name *", color = colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        cursorColor = Primary
                    )
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)", color = colors.textTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = colors.border,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        cursorColor = Primary
                    )
                )

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", color = colors.textTertiary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        ExerciseCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                // Muscle Group dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMuscle,
                    onExpandedChange = { expandedMuscle = !expandedMuscle }
                ) {
                    OutlinedTextField(
                        value = selectedMuscleGroup.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Muscle Group", color = colors.textTertiary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMuscle) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMuscle,
                        onDismissRequest = { expandedMuscle = false }
                    ) {
                        MuscleGroup.values().forEach { mg ->
                            DropdownMenuItem(
                                text = { Text(mg.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedMuscleGroup = mg
                                    expandedMuscle = false
                                }
                            )
                        }
                    }
                }

                // Difficulty dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedDifficulty,
                    onExpandedChange = { expandedDifficulty = !expandedDifficulty }
                ) {
                    OutlinedTextField(
                        value = selectedDifficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Difficulty", color = colors.textTertiary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDifficulty) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedDifficulty,
                        onDismissRequest = { expandedDifficulty = false }
                    ) {
                        Difficulty.values().forEach { diff ->
                            DropdownMenuItem(
                                text = { Text(diff.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedDifficulty = diff
                                    expandedDifficulty = false
                                }
                            )
                        }
                    }
                }

                // Equipment dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedEquipment,
                    onExpandedChange = { expandedEquipment = !expandedEquipment }
                ) {
                    OutlinedTextField(
                        value = selectedEquipment.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Equipment", color = colors.textTertiary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEquipment) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = colors.border,
                            focusedTextColor = colors.textPrimary,
                            unfocusedTextColor = colors.textPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEquipment,
                        onDismissRequest = { expandedEquipment = false }
                    ) {
                        Equipment.values().forEach { eq ->
                            DropdownMenuItem(
                                text = { Text(eq.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedEquipment = eq
                                    expandedEquipment = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = "Create",
                enabled = name.isNotBlank(),
                onClick = {
                    val exercise = Exercise(
                        name = name.trim(),
                        description = description.trim().ifBlank { "Custom exercise" },
                        category = selectedCategory,
                        muscleGroup = selectedMuscleGroup,
                        difficulty = selectedDifficulty,
                        equipment = selectedEquipment,
                        isCustom = true,
                        instructions = emptyList(),
                        tips = emptyList(),
                        imageUrl = null
                    )
                    onSave(exercise)
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




