package com.fitpulse.pro.data.content

import com.fitpulse.pro.data.model.FitnessArticle

class ArticleCatalog {
    val articles: List<FitnessArticle> = seedArticles()

    fun getById(id: String): FitnessArticle? = articles.find { it.id == id }
}
