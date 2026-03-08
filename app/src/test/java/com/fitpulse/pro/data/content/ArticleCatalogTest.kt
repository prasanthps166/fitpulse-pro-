package com.fitpulse.pro.data.content

import com.fitpulse.pro.data.model.DEFAULT_ARTICLE_DISCLAIMER
import com.fitpulse.pro.data.model.KnowledgeLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ArticleCatalogTest {

    private val catalog = ArticleCatalog()

    @Test
    fun articles_exposeStructuredKnowledgeEntries() {
        assertTrue(catalog.articles.size >= 11)
        assertNotNull(catalog.getById("strength_starter_plan"))
        assertNotNull(catalog.getById("creatine_monohydrate_full_guide"))
        assertNotNull(catalog.getById("fitness_fundamentals_full_guide"))
        assertNotNull(catalog.getById("fitness_dos_and_donts_full_guide"))
        assertNotNull(catalog.getById("common_fitness_myths_full_guide"))
    }

    @Test
    fun articles_includeLevelsSourcesAndDisclaimer() {
        catalog.articles.forEach { article ->
            assertTrue(article.levelsCovered.contains(article.primaryLevel))
            assertTrue(article.levelsCovered.isNotEmpty())
            assertTrue(article.sources.size >= 2)
            assertEquals("2026-03", article.lastUpdated)
            assertEquals(DEFAULT_ARTICLE_DISCLAIMER, article.disclaimer)
            assertTrue(article.beginner.stepByStep.isNotEmpty())
            assertTrue(article.intermediate.progressionExample.isNotEmpty())
            assertTrue(article.expert.latestEvidence.isNotEmpty())
        }

        assertEquals(
            KnowledgeLevel.entries.toSet(),
            catalog.articles.map { it.primaryLevel }.toSet()
        )
    }

    @Test
    fun articles_doNotContainCommonMojibakeSequences() {
        val combinedText = catalog.articles.joinToString(separator = "\n") { article ->
            buildString {
                appendLine(article.title)
                appendLine(article.quickTakeaway)
                appendLine(article.beginner.simpleExplanation)
                append(article.beginner.whyItMatters.joinToString("\n"))
                append(article.indiaFriendlyNotes.joinToString("\n"))
                append(article.sources.joinToString("\n") { source -> "${source.citation} ${source.title}" })
            }
        }

        assertFalse(combinedText.contains("â"))
        assertFalse(combinedText.contains("ð"))
    }
}
