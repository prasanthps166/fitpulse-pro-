package com.fitpulse.pro.ui.screens.learn

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.fitpulse.pro.data.model.ArticleCategory
import com.fitpulse.pro.data.model.FitnessArticle
import com.fitpulse.pro.data.model.KnowledgeLevel
import com.fitpulse.pro.ui.theme.Accent
import com.fitpulse.pro.ui.theme.Error
import com.fitpulse.pro.ui.theme.GradientMiddle
import com.fitpulse.pro.ui.theme.Info
import com.fitpulse.pro.ui.theme.Primary
import com.fitpulse.pro.ui.theme.Secondary
import com.fitpulse.pro.ui.theme.Success
import com.fitpulse.pro.ui.theme.Warning

internal fun ArticleCategory.displayName(): String = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> "Strength Training"
    ArticleCategory.HYPERTROPHY -> "Hypertrophy"
    ArticleCategory.ENDURANCE -> "Endurance"
    ArticleCategory.MOBILITY -> "Mobility"
    ArticleCategory.NUTRITION -> "Nutrition"
    ArticleCategory.RECOVERY -> "Recovery"
    ArticleCategory.INJURY_PREVENTION -> "Injury Prevention"
    ArticleCategory.SPECIAL_POPULATIONS -> "Special Populations"
    ArticleCategory.MENTAL_HEALTH -> "Mental Health"
    ArticleCategory.TRENDS_SCIENCE -> "Trends & Science"
}

internal fun ArticleCategory.accentColor(): Color = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> Primary
    ArticleCategory.HYPERTROPHY -> GradientMiddle
    ArticleCategory.ENDURANCE -> Info
    ArticleCategory.MOBILITY -> Accent
    ArticleCategory.NUTRITION -> Success
    ArticleCategory.RECOVERY -> Secondary
    ArticleCategory.INJURY_PREVENTION -> Warning
    ArticleCategory.SPECIAL_POPULATIONS -> Accent
    ArticleCategory.MENTAL_HEALTH -> Warning
    ArticleCategory.TRENDS_SCIENCE -> Info
}

internal fun ArticleCategory.icon(): ImageVector = when (this) {
    ArticleCategory.STRENGTH_TRAINING -> Icons.Default.FitnessCenter
    ArticleCategory.HYPERTROPHY -> Icons.AutoMirrored.Filled.TrendingUp
    ArticleCategory.ENDURANCE -> Icons.AutoMirrored.Filled.DirectionsRun
    ArticleCategory.MOBILITY -> Icons.Default.SelfImprovement
    ArticleCategory.NUTRITION -> Icons.Default.Restaurant
    ArticleCategory.RECOVERY -> Icons.Default.Hotel
    ArticleCategory.INJURY_PREVENTION -> Icons.Default.HealthAndSafety
    ArticleCategory.SPECIAL_POPULATIONS -> Icons.Default.School
    ArticleCategory.MENTAL_HEALTH -> Icons.Default.Psychology
    ArticleCategory.TRENDS_SCIENCE -> Icons.Default.Science
}

internal fun KnowledgeLevel.displayName(): String = when (this) {
    KnowledgeLevel.BEGINNER -> "Beginner"
    KnowledgeLevel.INTERMEDIATE -> "Intermediate"
    KnowledgeLevel.EXPERT -> "Expert"
}

internal fun KnowledgeLevel.accentColor(): Color = when (this) {
    KnowledgeLevel.BEGINNER -> Success
    KnowledgeLevel.INTERMEDIATE -> Warning
    KnowledgeLevel.EXPERT -> Error
}

internal fun FitnessArticle.coverageLabel(): String {
    val orderedLevels = levelsCovered
        .distinct()
        .sortedBy { level -> level.ordinal }

    return when (orderedLevels.size) {
        0 -> primaryLevel.displayName()
        1 -> orderedLevels.first().displayName()
        else -> "${orderedLevels.first().displayName()} to ${orderedLevels.last().displayName()}"
    }
}

internal fun FitnessArticle.matchesKnowledgeSearch(query: String): Boolean {
    if (query.isBlank()) {
        return true
    }

    val normalizedQuery = query.trim().lowercase()
    return title.lowercase().contains(normalizedQuery) ||
        quickTakeaway.lowercase().contains(normalizedQuery) ||
        category.displayName().lowercase().contains(normalizedQuery) ||
        tags.any { it.lowercase().contains(normalizedQuery) }
}

internal fun filterKnowledgeArticles(
    articles: List<FitnessArticle>,
    selectedCategory: ArticleCategory?,
    selectedKnowledgeLevel: KnowledgeLevel?,
    articleSearchQuery: String,
    showSavedOnly: Boolean,
    savedArticleIds: Set<String>,
    articlePriority: Map<String, Int>
): List<FitnessArticle> {
    return articles
        .filter { article ->
            val categoryMatch = selectedCategory == null || article.category == selectedCategory
            val levelMatch = selectedKnowledgeLevel?.let { article.primaryLevel == it } ?: true
            val savedMatch = !showSavedOnly || article.id in savedArticleIds
            categoryMatch && levelMatch && savedMatch && article.matchesKnowledgeSearch(articleSearchQuery)
        }
        .sortedWith(
            compareBy<FitnessArticle> { articlePriority[it.id] ?: Int.MAX_VALUE }
                .thenBy { it.title }
        )
}
