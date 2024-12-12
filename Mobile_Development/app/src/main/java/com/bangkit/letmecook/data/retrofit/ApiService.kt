package com.bangkit.letmecook.data.retrofit

import com.bangkit.letmecook.data.response.Article
import com.bangkit.letmecook.local.RecipeRequest
import com.bangkit.letmecook.local.entity.UserRequest
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.bangkit.letmecook.local.entity.ResponseML
import com.bangkit.letmecook.local.entity.ResponseMLItem
import com.bangkit.letmecook.local.entity.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("articles")
    suspend fun getArticles(): Response<List<Article>>

    @GET("inventories/{user_id}")
    fun getUserInventory(
        @Path("user_id") userId: Int
    ): Call<List<InventoryEntity>>

    @PUT("update-inventory/{id}")
    fun addInventory(
        @Body newInventory: InventoryEntity
    ): Call<Void>

    @DELETE("delete-inventory/{id}")
    fun deleteInventoryItem(
        @Path("id") inventoryId: Int
    ): Call<Void>

    @GET("search-inventory")
    fun searchInventory(
        @Query("query") query: String
    ): Call<List<InventoryEntity>>

    @POST("users")
    fun createUser(
        @Body user: UserRequest
    ): Call<UserResponse>

    @GET("ingredients")
    fun getIngredientsId(
        @Path("id") ingredientsId: Int
    ): Call<Void>

    @POST("api/predict")
    suspend fun getRecipes(
        @Body body: RecipeRequest
    ): Response<List<ResponseMLItem>>




}
