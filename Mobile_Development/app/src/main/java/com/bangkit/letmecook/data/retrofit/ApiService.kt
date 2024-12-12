package com.bangkit.letmecook.data.retrofit

import com.bangkit.letmecook.data.response.Article
import com.bangkit.letmecook.data.response.RecipeResponse
import com.bangkit.letmecook.local.entity.InventoryEntity
import okhttp3.RequestBody
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

    @GET("recipe/{id}")
    fun getDetailRecipes(

    )

    @POST("predict")
    fun generateRecipes(@Body requestBody: RequestBody): Call<List<RecipeResponse>>

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


}

