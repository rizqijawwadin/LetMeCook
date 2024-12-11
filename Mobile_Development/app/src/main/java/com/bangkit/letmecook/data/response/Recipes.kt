package com.bangkit.letmecook.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Recipes(

	@field:SerializedName("Recipes")
	val recipes: List<RecipesItem>
) : Parcelable

@Parcelize
data class RecipesItem(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("recipe_id")
	val recipeId: Int,

	@field:SerializedName("prep_time")
	val prepTime: String,

	@field:SerializedName("serves")
	val serves: Int,

	@field:SerializedName("name_recipe")
	val nameRecipe: String,

	@field:SerializedName("cooking_method")
	val cookingMethod: String
) : Parcelable
