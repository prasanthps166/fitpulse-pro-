package com.fitpulse.pro.domain.home

import com.fitpulse.pro.data.model.DailyStats
import com.fitpulse.pro.data.model.FitnessArticle
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutTemplate

enum class HomeFocusAction {
    RESUME_WORKOUT,
    START_WORKOUT,
    LOG_WATER,
    OPEN_KNOWLEDGE
}

data class HomeFocusMetric(
    val label: String,
    val value: String
)

data class HomeFocusState(
    val title: String,
    val subtitle: String,
    val actionLabel: String,
    val action: HomeFocusAction,
    val primaryMetric: HomeFocusMetric,
    val secondaryMetric: HomeFocusMetric,
    val suggestedTemplateId: Long? = null,
    val suggestedArticleId: String? = null
)

fun buildHomeFocusState(
    activeWorkout: Workout?,
    todayStats: DailyStats?,
    waterGoalMl: Int,
    recentWorkouts: List<Workout>,
    templates: List<WorkoutTemplate>,
    articles: List<FitnessArticle>
): HomeFocusState {
    val completedWorkoutCount = todayStats?.workoutCount ?: 0
    val waterMl = todayStats?.waterMl ?: 0
    val waterStatus = hydrationStatusLabel(currentMl = waterMl, goalMl = waterGoalMl)
    val suggestedTemplate = templates
        .sortedWith(
            compareByDescending<WorkoutTemplate> { it.timesUsed }
                .thenBy { it.difficulty.ordinal }
                .thenBy { it.name }
        )
        .firstOrNull()
    val suggestedArticle = articles.firstOrNull()

    if (activeWorkout != null) {
        return HomeFocusState(
            title = "Resume your in-progress workout",
            subtitle = "Pick up where you left off and finish ${activeWorkout.name}.",
            actionLabel = "Resume workout",
            action = HomeFocusAction.RESUME_WORKOUT,
            primaryMetric = HomeFocusMetric(
                label = "Workout",
                value = "Resume"
            ),
            secondaryMetric = HomeFocusMetric(
                label = "Queued",
                value = "${activeWorkout.exercises.orEmpty().size} exercises"
            )
        )
    }

    if (completedWorkoutCount == 0) {
        return HomeFocusState(
            title = "Get today's training done",
            subtitle = suggestedTemplate?.let {
                "${it.name} is ready when you are."
            } ?: "Start an empty workout and log your first session.",
            actionLabel = if (suggestedTemplate != null) "Start workout" else "Start empty workout",
            action = HomeFocusAction.START_WORKOUT,
            primaryMetric = HomeFocusMetric(
                label = "Workout",
                value = "Pending"
            ),
            secondaryMetric = HomeFocusMetric(
                label = "Hydration",
                value = waterStatus
            ),
            suggestedTemplateId = suggestedTemplate?.id
        )
    }

    if (waterMl < waterGoalMl / 2) {
        return HomeFocusState(
            title = "Catch up on hydration",
            subtitle = "Training is logged. Lock in recovery with a few quick water entries.",
            actionLabel = "Add 250 ml",
            action = HomeFocusAction.LOG_WATER,
            primaryMetric = HomeFocusMetric(
                label = "Workout",
                value = "Done"
            ),
            secondaryMetric = HomeFocusMetric(
                label = "Hydration",
                value = waterStatus
            )
        )
    }

    if (suggestedArticle != null) {
        return HomeFocusState(
            title = if (recentWorkouts.isEmpty()) {
                "Build your base with one good guide"
            } else {
                "Keep learning between sessions"
            },
            subtitle = "Open one guide now so the next workout is more intentional.",
            actionLabel = "Open knowledge",
            action = HomeFocusAction.OPEN_KNOWLEDGE,
            primaryMetric = HomeFocusMetric(
                label = "Workout",
                value = "Done"
            ),
            secondaryMetric = HomeFocusMetric(
                label = "Library",
                value = "${articles.size} guides"
            ),
            suggestedArticleId = suggestedArticle.id
        )
    }

    return HomeFocusState(
        title = "Keep the streak moving",
        subtitle = "The basics are handled. Start a short session or plan tomorrow now.",
        actionLabel = "Open workouts",
        action = HomeFocusAction.START_WORKOUT,
        primaryMetric = HomeFocusMetric(
            label = "Workout",
            value = "Done"
        ),
        secondaryMetric = HomeFocusMetric(
            label = "Hydration",
            value = waterStatus
        ),
        suggestedTemplateId = suggestedTemplate?.id
    )
}

private fun hydrationStatusLabel(currentMl: Int, goalMl: Int): String {
    if (goalMl <= 0) {
        return "${currentMl} ml"
    }

    return "${currentMl} / ${goalMl} ml"
}
