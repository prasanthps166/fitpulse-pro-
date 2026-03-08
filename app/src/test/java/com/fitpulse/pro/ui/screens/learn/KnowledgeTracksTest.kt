package com.fitpulse.pro.ui.screens.learn

import com.fitpulse.pro.data.content.ArticleCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KnowledgeTracksTest {

    private val articles = ArticleCatalog().articles

    @Test
    fun buildKnowledgeTracks_exposesCoreKnowledgeRoadmap() {
        val tracks = buildKnowledgeTracks(articles)

        assertEquals(
            listOf(
                "start_here",
                "fitness_fundamentals",
                "strength_training_basics",
                "nutrition_basics",
                "recovery_basics",
                "programming_basics",
                "muscle_gain_basics",
                "fat_loss_basics",
                "fitness_dos_and_donts",
                "common_fitness_myths"
            ),
            tracks.map { it.id }
        )
        assertEquals("fitness_fundamentals_full_guide", tracks.first().leadArticle.id)
        assertTrue(tracks.all { it.articles.size >= 3 })
        assertTrue(tracks.all { it.totalReadTimeMinutes > 0 })
    }

    @Test
    fun recommendedKnowledgeArticleOrder_prioritizesCoreGuidesFirst() {
        val orderedIds = recommendedKnowledgeArticleOrder(articles)

        assertEquals(
            listOf(
                "fitness_fundamentals_full_guide",
                "strength_starter_plan",
                "protein_macros_muscle_fat_loss"
            ),
            orderedIds.take(3)
        )
        assertTrue(orderedIds.indexOf("fitness_dos_and_donts_full_guide") < orderedIds.indexOf("hiit_vs_steady_state"))
        assertTrue(orderedIds.contains("common_fitness_myths_full_guide"))
    }

    @Test
    fun relatedKnowledgeArticles_prioritizesSharedTracksAndExcludesCurrentArticle() {
        val article = articles.first { it.id == "strength_starter_plan" }

        val related = relatedKnowledgeArticles(
            article = article,
            articles = articles,
            limit = 3
        )

        assertEquals(3, related.size)
        assertFalse(related.any { it.id == article.id })
        assertEquals("progressive_overload_full_guide", related.first().id)
        assertTrue(related.map { it.id }.contains("protein_macros_muscle_fat_loss"))
    }
}
