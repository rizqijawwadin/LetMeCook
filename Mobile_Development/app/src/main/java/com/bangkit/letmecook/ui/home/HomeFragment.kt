package com.bangkit.letmecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.local.entity.InventoryEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var expiredStockAdapter: ExpiredStockAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // RecyclerView untuk artikel
        val articleRecyclerView = view.findViewById<RecyclerView>(R.id.rvNews)
        val progressBarNews = view.findViewById<ProgressBar>(R.id.progressBarNews)

        // RecyclerView untuk stok kedaluwarsa
        val expiredStockRecyclerView = view.findViewById<RecyclerView>(R.id.rvExpiredStock)
        val progressBarOutStock = view.findViewById<ProgressBar>(R.id.progressBarOutStock)

        // Inisialisasi ViewModel untuk artikel
        viewModel = ViewModelProvider(
            this,
            ArticleViewModelFactory(ArticleRepository(ApiConfig.getApiService()))
        ).get(HomeViewModel::class.java)

        // Observasi artikel
        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter = ArticleAdapter(articles) { article ->
                val action = HomeFragmentDirections.actionNavigationHomeToDetailArticleFragment(article)
                findNavController().navigate(action)
            }
            articleRecyclerView.adapter = articleAdapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBarNews.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Load artikel
        viewModel.loadArticles()
        articleRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Setup RecyclerView untuk stok kedaluwarsa
        expiredStockAdapter = ExpiredStockAdapter()
        expiredStockRecyclerView.apply {
            adapter = expiredStockAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // Load stok kedaluwarsa
        loadExpiredStocks(progressBarOutStock)

        return view
    }

    private fun loadExpiredStocks(progressBar: ProgressBar) {
        val apiService = ApiConfig.getApiService()
        val userId = 2 // Replace with actual user ID
        val sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds

        progressBar.visibility = View.VISIBLE
        apiService.getUserInventory(userId).enqueue(object : Callback<List<InventoryEntity>> {
            override fun onResponse(
                call: Call<List<InventoryEntity>>,
                response: Response<List<InventoryEntity>>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val stocks = response.body()
                    val currentTime = System.currentTimeMillis()

                    // Filter and map expired stocks
                    val expiredStocks = stocks?.mapNotNull { stock ->
                        val expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(stock.expiry_date)
                        expiryDate?.time?.let { expiryTime ->
                            val timeDifference = expiryTime - currentTime
                            if (timeDifference in 1..sevenDaysInMillis) {
                                val daysLeft = (timeDifference / (24 * 60 * 60 * 1000)).toInt()
                                stock to "$daysLeft days left"
                            } else {
                                null
                            }
                        }
                    }?.sortedBy { (stock, _) ->
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(stock.expiry_date)?.time
                    } ?: emptyList()

                    // Update RecyclerView
                    expiredStockAdapter.submitList(expiredStocks.map { it.first }) // Only pass the stock data
                    expiredStockAdapter.setDaysLeftLabels(expiredStocks.map { it.second }) // Pass labels
                } else {
                    Toast.makeText(requireContext(), "Failed to load stocks", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<InventoryEntity>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
