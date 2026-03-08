package com.fitpulse.pro.data.model

const val DEFAULT_ARTICLE_DISCLAIMER =
    "This information is for educational purposes only and is based on general research. " +
        "It is not personalized medical or training advice. Consult a doctor or qualified " +
        "professional before starting any exercise or nutrition change, especially if you " +
        "have health conditions or injuries."

data class FitnessArticle(
    val id: String,
    val title: String,
    val category: ArticleCategory,
    val primaryLevel: KnowledgeLevel,
    val levelsCovered: List<KnowledgeLevel>,
    val quickTakeaway: String,
    val beginner: BeginnerSection,
    val intermediate: IntermediateSection,
    val expert: ExpertSection,
    val indiaFriendlyNotes: List<String> = emptyList(),
    val sources: List<ArticleSource> = emptyList(),
    val author: String = "FitPulse Research Team",
    val readTimeMinutes: Int = 8,
    val tags: List<String> = emptyList(),
    val lastUpdated: String = "2026-03",
    val disclaimer: String = DEFAULT_ARTICLE_DISCLAIMER
) {
    val summary: String
        get() = quickTakeaway
}

data class BeginnerSection(
    val simpleExplanation: String,
    val whyItMatters: List<String>,
    val stepByStep: List<String>,
    val equipmentNeeded: List<String>,
    val commonMistakes: List<String>,
    val safetyTips: List<String>
)

data class IntermediateSection(
    val progressGuidance: List<String>,
    val keyPrinciples: List<String>,
    val progressionExample: List<String>,
    val weeklyIntegration: List<String>,
    val trackingTips: List<String>
)

data class ExpertSection(
    val biomechanicsAndActivation: List<String>,
    val latestEvidence: List<String>,
    val advancedVariables: List<String>,
    val researchBackedTweaks: List<String>
)

data class ArticleSource(
    val title: String,
    val citation: String,
    val url: String
)

enum class KnowledgeLevel {
    BEGINNER, INTERMEDIATE, EXPERT
}

enum class ArticleCategory {
    STRENGTH_TRAINING,
    HYPERTROPHY,
    ENDURANCE,
    MOBILITY,
    NUTRITION,
    RECOVERY,
    INJURY_PREVENTION,
    SPECIAL_POPULATIONS,
    MENTAL_HEALTH,
    TRENDS_SCIENCE
}

data class CoachTip(
    val id: String,
    val message: String,
    val category: TipCategory,
    val priority: Int = 0,
    val actionLabel: String? = null,
    val actionRoute: String? = null
)

enum class TipCategory {
    WORKOUT_SUGGESTION, NUTRITION_TIP, RECOVERY, MOTIVATION, FORM_TIP, MILESTONE
}
