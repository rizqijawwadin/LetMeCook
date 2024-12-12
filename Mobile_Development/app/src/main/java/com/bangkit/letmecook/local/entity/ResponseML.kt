package com.bangkit.letmecook.local.entity

import com.google.gson.annotations.SerializedName

data class ResponseML(
	@field:SerializedName("responseML")
	val responseML: List<ResponseMLItem?>? = null
)
