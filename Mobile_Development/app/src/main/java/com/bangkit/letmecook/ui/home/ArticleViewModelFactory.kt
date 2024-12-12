package com.bangkit.letmecook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ArticleViewModelFactory(private val repository: ArticleRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
