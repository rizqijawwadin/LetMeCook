package com.bangkit.letmecook.data.retrofit

import com.bangkit.letmecook.data.response.Article
import com.bangkit.letmecook.data.response.Category
import com.bangkit.letmecook.data.response.Recipes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("articles")
    suspend fun getArticles(): Response<List<Article>>

    @POST("/predict")
    suspend fun generateRecipes(): Response<List<Recipes>>

    @GET("/recipe-category")
    suspend fun getRecipeCategories(): Response<List<Category>>

    @POST("/categories")
    suspend fun getRecipesByCategory(@Body category: String): Response<List<Recipes>>

    @GET("/recipe-details/{id}")
    suspend fun getRecipeDetails(@Path("id") recipeId: String): Response<Recipes>

}

