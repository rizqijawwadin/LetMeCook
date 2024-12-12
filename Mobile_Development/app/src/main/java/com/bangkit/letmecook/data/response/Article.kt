package com.bangkit.letmecook.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(

	val id: Int,
	val image: String,
	val title: String,
	val body: String,
	val url: String
) : Parcelable

