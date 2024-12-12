package com.bangkit.letmecook.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeResponse(
    val recipe_id: String,
    val name_recipe: String,
    val image: String,
    val categories: List<String>
) : Parcelable

