package com.bangkit.letmecook.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.letmecook.data.response.Article
import com.bangkit.letmecook.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _articles.value = repository.fetchArticles()
            } catch (e: Exception) {
                Log.e("ArticleViewModel", "Error fetching articles: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}