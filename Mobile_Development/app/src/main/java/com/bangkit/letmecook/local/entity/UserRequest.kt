package com.bangkit.letmecook.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRequest(
    val name: String,
    val email: String,
    val password: String,
    val profilePicture: String? = null
): Parcelable

@Parcelize
data class UserResponse(
    val userId: Int,
    val message: String
): Parcelable

