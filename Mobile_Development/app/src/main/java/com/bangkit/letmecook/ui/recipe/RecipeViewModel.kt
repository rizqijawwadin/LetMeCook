package com.bangkit.letmecook.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.letmecook.data.response.Category
import com.bangkit.letmecook.data.response.Recipes
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.data.retrofit.ApiService
import kotlinx.coroutines.launch

class RecipeViewModel(private val instance: ApiService) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipes>>()
    val recipes: LiveData<List<Recipes>> get() = _recipes

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = instance.getRecipeCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = instance.generateRecipes()
                if (response.isSuccessful) {
                    _recipes.value = response.body()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
