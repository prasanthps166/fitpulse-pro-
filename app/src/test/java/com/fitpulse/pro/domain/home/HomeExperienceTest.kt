package com.fitpulse.pro.domain.home

import com.fitpulse.pro.data.model.ArticleCategory
import com.fitpulse.pro.data.model.BeginnerSection
import com.fitpulse.pro.data.model.ExpertSection
import com.fitpulse.pro.data.model.FitnessArticle
import com.fitpulse.pro.data.model.IntermediateSection
import com.fitpulse.pro.data.model.KnowledgeLevel
import com.fitpulse.pro.data.model.Workout
import com.fitpulse.pro.data.model.WorkoutExercise
import com.fitpulse.pro.data.model.WorkoutTemplate
import com.fitpulse.pro.data.model.DailyStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeExperienceTest {

    @Test
    fun buildHomeFocusState_prioritizesResumingActiveWorkout() {
        val state = buildHomeFocusState(
            activeWorkout = Workout(
                name = "Upper Body",
                exercises = listOf(
                    WorkoutExercise(exerciseId = 1L, exerciseName = "Bench Press")
                )
            ),
            todayStats = DailyStats(date = "2026-03-08", workoutCount = 0, waterMl = 300),
            waterGoalMl = 3000,
            recentWorkouts = emptyList(),
            templates = emptyList(),
            articles = emptyList()
        )

        assertEquals(HomeFocusAction.RESUME_WORKOUT, state.action)
        assertEquals("Resume workout", state.actionLabel)
        assertEquals("Queued", state.secondaryMetric.label)
    }

    @Test
    fun buildHomeFocusState_prioritizesTrainingBeforeEverythingElse() {
        val template = WorkoutTemplate(id = 42L, name = "Full Body A", estimatedDurationMinutes = 45)

        val state = buildHomeFocusState(
            activeWorkout = null,
            todayStats = DailyStats(date = "2026-03-08", workoutCount = 0, waterMl = 900),
            waterGoalMl = 3000,
            recentWorkouts = emptyList(),
            templates = listOf(template),
            articles = listOf(sampleArticle())
        )

        assertEquals(HomeFocusAction.START_WORKOUT, state.action)
        assertEquals(42L, state.suggestedTemplateId)
        assertEquals("Pending", state.primaryMetric.value)
    }

    @Test
    fun buildHomeFocusState_promptsHydrationAfterWorkoutIfWaterIsLow() {
        val state = buildHomeFocusState(
            activeWorkout = null,
            todayStats = DailyStats(date = "2026-03-08", workoutCount = 1, waterMl = 600),
            waterGoalMl = 3000,
            recentWorkouts = listOf(Workout(name = "Leg Day")),
            templates = emptyList(),
            articles = listOf(sampleArticle())
        )

        assertEquals(HomeFocusAction.LOG_WATER, state.action)
        assertEquals("Add 250 ml", state.actionLabel)
        assertNull(state.suggestedArticleId)
    }

    @Test
    fun buildHomeFocusState_usesKnowledgeWhenTrainingAndHydrationAreOnTrack() {
        val article = sampleArticle()

        val state = buildHomeFocusState(
            activeWorkout = null,
            todayStats = DailyStats(date = "2026-03-08", workoutCount = 1, waterMl = 2200),
            waterGoalMl = 3000,
            recentWorkouts = listOf(Workout(name = "Push Day")),
            templates = emptyList(),
            articles = listOf(article)
        )

        assertEquals(HomeFocusAction.OPEN_KNOWLEDGE, state.action)
        assertEquals(article.id, state.suggestedArticleId)
        assertEquals("Library", state.secondaryMetric.label)
    }

    private fun sampleArticle() = FitnessArticle(
        id = "fitness_fundamentals",
        title = "Fitness Fundamentals",
        category = ArticleCategory.STRENGTH_TRAINING,
        quickTakeaway = "Learn the basics.",
        primaryLevel = KnowledgeLevel.BEGINNER,
        levelsCovered = listOf(KnowledgeLevel.BEGINNER, KnowledgeLevel.INTERMEDIATE),
        beginner = BeginnerSection(
            simpleExplanation = "Basics first.",
            whyItMatters = listOf("Consistency compounds."),
            stepByStep = listOf("Train three times per week."),
            equipmentNeeded = listOf("Shoes"),
            commonMistakes = listOf("Skipping workouts"),
            safetyTips = listOf("Progress gradually")
        ),
        intermediate = IntermediateSection(
            progressGuidance = listOf("Increase reps first."),
            keyPrinciples = listOf("Volume and progression matter."),
            progressionExample = listOf("Add one rep before load."),
            weeklyIntegration = listOf("Train three times weekly."),
            trackingTips = listOf("Track top sets.")
        ),
        expert = ExpertSection(
            biomechanicsAndActivation = listOf("Manage joint positions."),
            latestEvidence = listOf("Research supports progressive overload."),
            advancedVariables = listOf("Adjust volume to fatigue."),
            researchBackedTweaks = listOf("Use fatigue wisely.")
        ),
        tags = listOf("basics")
    )
}
