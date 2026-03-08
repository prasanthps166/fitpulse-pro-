package com.fitpulse.pro.ui.screens.learn

import com.fitpulse.pro.data.model.FitnessArticle

internal data class KnowledgeTrack(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val articleIds: List<String>,
    val articles: List<FitnessArticle>
) {
    val leadArticle: FitnessArticle
        get() = articles.first()

    val totalReadTimeMinutes: Int
        get() = articles.sumOf { it.readTimeMinutes }
}

internal fun buildKnowledgeTracks(articles: List<FitnessArticle>): List<KnowledgeTrack> {
    val articleById = articles.associateBy { it.id }

    return knowledgeTrackSpecs.mapNotNull { spec ->
        val trackArticles = spec.articleIds.mapNotNull(articleById::get)
        if (trackArticles.isEmpty()) {
            null
        } else {
            KnowledgeTrack(
                id = spec.id,
                title = spec.title,
                subtitle = spec.subtitle,
                description = spec.description,
                articleIds = spec.articleIds,
                articles = trackArticles
            )
        }
    }
}

internal fun recommendedKnowledgeArticleOrder(articles: List<FitnessArticle>): List<String> {
    val trackPriority = buildKnowledgeTracks(articles)
        .flatMap { it.articleIds }
        .distinct()

    return (trackPriority + articles.map { it.id }).distinct()
}

internal fun relatedKnowledgeArticles(
    article: FitnessArticle,
    articles: List<FitnessArticle>,
    limit: Int = 3
): List<FitnessArticle> {
    if (articles.isEmpty() || limit <= 0) {
        return emptyList()
    }

    val articlePriority = recommendedKnowledgeArticleOrder(articles)
        .withIndex()
        .associate { it.value to it.index }
    val trackIdsByArticle = buildKnowledgeTracks(articles)
        .flatMap { track -> track.articles.map { it.id to track.id } }
        .groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
        .mapValues { (_, trackIds) -> trackIds.toSet() }
    val normalizedSourceTags = article.tags.map { it.lowercase() }.toSet()
    val sourceTrackIds = trackIdsByArticle[article.id].orEmpty()

    return articles
        .asSequence()
        .filter { candidate -> candidate.id != article.id }
        .sortedWith(
            compareByDescending<FitnessArticle> { candidate ->
                val candidateTrackIds = trackIdsByArticle[candidate.id].orEmpty()
                val normalizedCandidateTags = candidate.tags.map { it.lowercase() }.toSet()
                val sharedTracks = sourceTrackIds.intersect(candidateTrackIds).size
                val sharedTags = normalizedSourceTags.intersect(normalizedCandidateTags).size
                val sameCategory = if (candidate.category == article.category) 1 else 0

                (sharedTracks * 100) + (sameCategory * 30) + (sharedTags * 10)
            }.thenBy { candidate -> articlePriority[candidate.id] ?: Int.MAX_VALUE }
                .thenBy { candidate -> candidate.title }
        )
        .take(limit)
        .toList()
}

private data class KnowledgeTrackSpec(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val articleIds: List<String>
)

private val knowledgeTrackSpecs = listOf(
    KnowledgeTrackSpec(
        id = "start_here",
        title = "Start Here",
        subtitle = "3 essential guides",
        description = "Learn the basics in the right order before chasing advanced tactics.",
        articleIds = listOf(
            "fitness_fundamentals_full_guide",
            "strength_starter_plan",
            "protein_macros_muscle_fat_loss"
        )
    ),
    KnowledgeTrackSpec(
        id = "fitness_fundamentals",
        title = "Fitness Fundamentals",
        subtitle = "The basics that drive most results",
        description = "Build the core understanding most people skip: consistency, recovery, and good defaults.",
        articleIds = listOf(
            "fitness_fundamentals_full_guide",
            "fitness_dos_and_donts_full_guide",
            "common_fitness_myths_full_guide"
        )
    ),
    KnowledgeTrackSpec(
        id = "strength_training_basics",
        title = "Strength Training Basics",
        subtitle = "Patterns, setup, and progression",
        description = "Learn how to train the main movement patterns well before chasing fancy programming.",
        articleIds = listOf(
            "strength_starter_plan",
            "warm_up_and_injury_prevention",
            "progressive_overload_full_guide"
        )
    ),
    KnowledgeTrackSpec(
        id = "nutrition_basics",
        title = "Nutrition Basics",
        subtitle = "Calories, protein, and useful supplements",
        description = "Cover the few nutrition decisions that matter far more than timing hacks and gimmicks.",
        articleIds = listOf(
            "protein_macros_muscle_fat_loss",
            "fitness_fundamentals_full_guide",
            "creatine_monohydrate_full_guide"
        )
    ),
    KnowledgeTrackSpec(
        id = "recovery_basics",
        title = "Recovery Basics",
        subtitle = "Sleep, fatigue, and joint-friendly training",
        description = "Build results you can recover from, repeat, and keep for years.",
        articleIds = listOf(
            "sleep_recovery_deloads",
            "daily_mobility_for_desk_lifters",
            "warm_up_and_injury_prevention"
        )
    ),
    KnowledgeTrackSpec(
        id = "programming_basics",
        title = "Programming Basics",
        subtitle = "How to structure weeks that work",
        description = "Turn good exercises into a repeatable week with sane volume, progression, and recovery.",
        articleIds = listOf(
            "strength_starter_plan",
            "progressive_overload_full_guide",
            "fitness_dos_and_donts_full_guide"
        )
    ),
    KnowledgeTrackSpec(
        id = "muscle_gain_basics",
        title = "Muscle Gain Basics",
        subtitle = "Training, food, and overload",
        description = "Focus on the training and nutrition levers that actually move muscle growth.",
        articleIds = listOf(
            "strength_starter_plan",
            "progressive_overload_full_guide",
            "protein_macros_muscle_fat_loss"
        )
    ),
    KnowledgeTrackSpec(
        id = "fat_loss_basics",
        title = "Fat Loss Basics",
        subtitle = "Energy balance and repeatable activity",
        description = "Use cardio, protein, and daily habits as support tools instead of punishment.",
        articleIds = listOf(
            "fitness_fundamentals_full_guide",
            "protein_macros_muscle_fat_loss",
            "hiit_vs_steady_state"
        )
    ),
    KnowledgeTrackSpec(
        id = "fitness_dos_and_donts",
        title = "Fitness Do's And Don'ts",
        subtitle = "Good defaults and avoidable mistakes",
        description = "The fastest way to improve is often stopping the common mistakes that slow people down.",
        articleIds = listOf(
            "fitness_dos_and_donts_full_guide",
            "warm_up_and_injury_prevention",
            "sleep_recovery_deloads"
        )
    ),
    KnowledgeTrackSpec(
        id = "common_fitness_myths",
        title = "Common Fitness Myths",
        subtitle = "Evidence over hype",
        description = "Pressure-test dramatic claims so you can think clearly about training and nutrition.",
        articleIds = listOf(
            "common_fitness_myths_full_guide",
            "fitness_fundamentals_full_guide",
            "hiit_vs_steady_state"
        )
    )
)
