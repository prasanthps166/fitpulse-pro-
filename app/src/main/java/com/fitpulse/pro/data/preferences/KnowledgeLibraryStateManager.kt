package com.fitpulse.pro.data.preferences

import android.content.Context

class KnowledgeLibraryStateManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadSavedArticleIds(): Set<String> {
        return prefs.getStringSet(KEY_SAVED_ARTICLES, emptySet()).orEmpty()
    }

    fun setSavedArticleIds(articleIds: Set<String>): Set<String> {
        prefs.edit().putStringSet(KEY_SAVED_ARTICLES, articleIds).apply()
        return loadSavedArticleIds()
    }

    fun loadLastReadArticleId(): String? {
        return prefs.getString(KEY_LAST_READ_ARTICLE, null)
    }

    fun setLastReadArticleId(articleId: String): String {
        prefs.edit().putString(KEY_LAST_READ_ARTICLE, articleId).apply()
        return articleId
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "fitpulse_knowledge_prefs"
        private const val KEY_SAVED_ARTICLES = "saved_articles"
        private const val KEY_LAST_READ_ARTICLE = "last_read_article"
    }
}
