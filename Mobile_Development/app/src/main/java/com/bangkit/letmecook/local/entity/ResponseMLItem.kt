package com.bangkit.letmecook.local.entity

import com.google.gson.annotations.SerializedName

data class ResponseMLItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("recipe_id")
	val recipeId: Int? = null,

	@field:SerializedName("name_recipe")
	val nameRecipe: String? = null,

	@field:SerializedName("categories")
	val categories: List<String?>? = null
)