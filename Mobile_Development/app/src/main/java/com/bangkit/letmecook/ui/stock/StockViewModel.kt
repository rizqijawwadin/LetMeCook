package com.bangkit.letmecook.ui.stock

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.local.entity.InventoryEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<InventoryEntity>>()
    val searchResults: LiveData<List<InventoryEntity>> = _searchResults

    fun searchStock(query: String) {
        val call = ApiConfig.getApiService().searchInventory(query = query)
        call.enqueue(object : Callback<List<InventoryEntity>> {
            override fun onResponse(call: Call<List<InventoryEntity>>, response: Response<List<InventoryEntity>>) {
                if (response.isSuccessful) {
                    _searchResults.value = response.body()
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<InventoryEntity>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}