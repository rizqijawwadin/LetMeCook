package com.bangkit.letmecook.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Category(

	@field:SerializedName("Category")
	val category: List<CategoryItem>
) : Parcelable

@Parcelize
data class CategoryItem(

	@field:SerializedName("category_name")
	val categoryName: String,

	@field:SerializedName("category_id")
	val categoryId: Int
) : Parcelable
