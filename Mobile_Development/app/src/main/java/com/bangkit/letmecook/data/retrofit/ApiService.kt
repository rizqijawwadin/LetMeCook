package com.bangkit.letmecook.data.retrofit

import com.bangkit.letmecook.data.response.Article
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("articles")
    suspend fun getArticles(): Response<List<Article>>
}
