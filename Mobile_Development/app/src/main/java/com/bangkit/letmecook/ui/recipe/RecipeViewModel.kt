package com.bangkit.letmecook.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.letmecook.data.response.RecipeItem
import com.bangkit.letmecook.local.entity.InventoryEntity

class RecipeViewModel : ViewModel() {
    // LiveData untuk menyimpan bahan dari inventori
    private val _stockItems = MutableLiveData<List<InventoryEntity>>()
    val stockItems: LiveData<List<InventoryEntity>> = _stockItems

    // LiveData untuk menyimpan daftar resep yang direkomendasikan
    private val _recipes = MutableLiveData<List<RecipeItem>>()
    val recipes: LiveData<List<RecipeItem>> = _recipes

    // Fungsi untuk update LiveData bahan dan resep akan didefinisikan di sini
}
