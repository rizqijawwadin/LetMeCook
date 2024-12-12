package com.bangkit.letmecook.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.letmecook.data.retrofit.ApiService
import com.bangkit.letmecook.databinding.FragmentRecipeBinding
import com.bangkit.letmecook.local.RecipeRequest
import com.bangkit.letmecook.local.entity.ResponseMLItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var recipeAdapter: RecipeAdapter

    private val selectedIngredients = mutableListOf<String>()

    companion object {
        private const val TAG = "RecipeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)

        setupIngredientRecyclerView()
        setupGenerateButton()

        return binding.root
    }

    private fun setupIngredientRecyclerView() {
        ingredientAdapter = IngredientAdapter { ingredient, isChecked ->
            if (isChecked) {
                selectedIngredients.add(ingredient)
            } else {
                selectedIngredients.remove(ingredient)
            }
        }

        binding.rvIngredients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientAdapter
        }

        val ingredients = listOf("Egg", "Tomato", "Cheese", "Milk", "Salt")


        ingredientAdapter.submitList(ingredients)
    }

    private fun setupGenerateButton() {
        binding.btnGenerateRecipe.setOnClickListener {
            if (selectedIngredients.isNotEmpty()) {
                generateRecipes(selectedIngredients)
            } else {
                Toast.makeText(requireContext(), "Please select ingredients", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateRecipes(ingredients: List<String>) {
        val apiService = Retrofit.Builder()
            .baseUrl("https://letmecookapi-1054843139157.asia-southeast2.run.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = RecipeRequest(data = ingredients)
                Log.d(TAG, "Request body: $requestBody")

                val response = apiService.getRecipes(requestBody)
                if (response.isSuccessful) {
                    Log.d(TAG, "API request successful: ${response.body()}")
                    val recipes = response.body() ?: emptyList()
                    withContext(Dispatchers.Main) {
                        setupRecipeRecyclerView(recipes)
                    }
                } else {
                    Log.e(TAG, "API request failed: ${response.code()} - ${response.message()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch recipes: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "API request error", e)
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun setupRecipeRecyclerView(recipes: List<ResponseMLItem?>) {
        recipeAdapter = RecipeAdapter()
        binding.rvRecipes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }
        recipeAdapter.submitList(recipes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}