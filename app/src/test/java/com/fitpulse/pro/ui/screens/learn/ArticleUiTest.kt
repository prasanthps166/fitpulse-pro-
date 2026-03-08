package com.fitpulse.pro.ui.screens.learn

import com.fitpulse.pro.data.content.ArticleCatalog
import com.fitpulse.pro.data.model.ArticleCategory
import com.fitpulse.pro.data.model.KnowledgeLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleUiTest {

    private val articles = ArticleCatalog().articles
    private val articlePriority = recommendedKnowledgeArticleOrder(articles)
        .withIndex()
        .associate { it.value to it.index }

    @Test
    fun matchesKnowledgeSearch_matchesTitleCategoryAndTags() {
        val article = articles.first { it.id == "progressive_overload_full_guide" }

        assertTrue(article.matchesKnowledgeSearch("progressive"))
        assertTrue(article.matchesKnowledgeSearch("hypertrophy"))
        assertTrue(article.matchesKnowledgeSearch(article.tags.first()))
    }

    @Test
    fun filterKnowledgeArticles_appliesSavedCategoryLevelAndQuery() {
        val filtered = filterKnowledgeArticles(
            articles = articles,
            selectedCategory = ArticleCategory.NUTRITION,
            selectedKnowledgeLevel = KnowledgeLevel.BEGINNER,
            articleSearchQuery = "protein",
            showSavedOnly = true,
            savedArticleIds = setOf("protein_macros_muscle_fat_loss"),
            articlePriority = articlePriority
        )

        assertEquals(1, filtered.size)
        assertEquals("protein_macros_muscle_fat_loss", filtered.first().id)
    }

    @Test
    fun coverageLabel_describesLevelRange() {
        val article = articles.first { it.id == "fitness_fundamentals_full_guide" }

        assertEquals("Beginner to Expert", article.coverageLabel())
    }
}
