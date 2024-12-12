package com.bangkit.letmecook.local.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InventoryEntity(
    val user_id_user: Int?,
    val ingredient_id_ingredient: Int?,
    val id_inventory: Int,
    val ingredients_pic: String? = null,
    val buy_date: String? = null,
    val stock: Int? = null,
    val unit: String? = null,
    val place: String? = null,
    val expiry_date: String? = null,
    val ingredient_name: String
): Parcelable
