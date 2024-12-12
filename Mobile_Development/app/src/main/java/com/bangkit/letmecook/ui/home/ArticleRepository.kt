package com.bangkit.letmecook.ui.home

import com.bangkit.letmecook.data.retrofit.ApiService

import com.bangkit.letmecook.data.response.Article

class ArticleRepository(private val apiService: ApiService) {

    suspend fun fetchArticles(): List<Article> {
        val response = apiService.getArticles() // Pastikan ini adalah fungsi di ApiService
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to fetch articles: ${response.message()}")
        }
    }
}
