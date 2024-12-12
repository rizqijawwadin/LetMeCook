package com.bangkit.letmecook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.data.response.Article
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.FragmentHomeBinding
import com.bangkit.letmecook.ui.profile.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvNews)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarNews)

        viewModel = ViewModelProvider(
            this,
            ArticleViewModelFactory(ArticleRepository(ApiConfig.getApiService()))
        ).get(HomeViewModel::class.java)


        viewModel.articles.observe(viewLifecycleOwner) { articles ->
            adapter = ArticleAdapter(articles) { article ->
                val action = HomeFragmentDirections.actionNavigationHomeToDetailArticleFragment(article)
                findNavController().navigate(action)
            }
            recyclerView.adapter = adapter
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.loadArticles()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }
}
