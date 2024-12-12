package com.bangkit.letmecook.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.letmecook.data.response.RecipeResponse
import com.bangkit.letmecook.data.response.RequestData
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.FragmentRecipeBinding
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {

    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private lateinit var checkboxAdapter: CheckboxAdapter
    private lateinit var recipeListAdapter: RecipeListAdapter

    private val selectedIngredients = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)

        // Muat data inventory dan setup RecyclerView
        loadInventoryData { inventory ->
            setupCheckboxRecyclerView(inventory)
        }
        setupRecipeRecyclerView()
        setupGenerateButton()

        return binding.root
    }

    private fun setupCheckboxRecyclerView(inventory: List<InventoryEntity>) {
        checkboxAdapter = CheckboxAdapter(inventory) { item, isChecked ->
            if (isChecked) {
                selectedIngredients.add(item.ingredient_name)
            } else {
                selectedIngredients.remove(item.ingredient_name)
            }
        }

        binding.rvCheckboxItem.apply {
            adapter = checkboxAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun loadInventoryData(onInventoryLoaded: (List<InventoryEntity>) -> Unit) {
        val apiService = ApiConfig.getApiService()
        apiService.getUserInventory(1).enqueue(object : Callback<List<InventoryEntity>> {
            override fun onResponse(
                call: Call<List<InventoryEntity>>,
                response: Response<List<InventoryEntity>>
            ) {
                if (response.isSuccessful) {
                    val inventory = response.body() ?: emptyList()
                    onInventoryLoaded(inventory)
                } else {
                    showToast("Failed to load inventory")
                }
            }

            override fun onFailure(call: Call<List<InventoryEntity>>, t: Throwable) {
                showToast("Error: ${t.message ?: "Unknown error"}")
            }
        })
    }

    private fun setupRecipeRecyclerView() {
        recipeListAdapter = RecipeListAdapter(emptyList())
        binding.rvRecipes.apply {
            adapter = recipeListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupGenerateButton() {
        binding.btnGenerateRecipe.setOnClickListener {
            if (selectedIngredients.isEmpty()) {
                showToast("Please select at least one ingredient")
                return@setOnClickListener
            }
            generateRecipes(selectedIngredients)
        }
    }

    private fun generateRecipes(ingredients: List<String>) {
        val apiService = ApiConfig.getApiService()

        // Membuat instance RequestData
        val requestData = RequestData(data = ingredients)

        // Mengonversi RequestData menjadi JSON menggunakan Gson
        val jsonRequest = Gson().toJson(requestData)

        // Membuat RequestBody dari JSON string dengan MediaType
        val requestBody = RequestBody.create("application/json".toMediaType(), jsonRequest)

        binding.progressBarRecipe.visibility = View.VISIBLE // Tampilkan ProgressBar

        apiService.generateRecipes(requestBody).enqueue(object : Callback<List<RecipeResponse>> {
            override fun onResponse(
                call: Call<List<RecipeResponse>>,
                response: Response<List<RecipeResponse>>
            ) {
                binding.progressBarRecipe.visibility = View.GONE // Sembunyikan ProgressBar

                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()

                    // Batasi jumlah resep yang ditampilkan (misalnya 5 resep pertama)
                    val limitedRecipes = recipes.take(15)

                    // Update data pada adapter dengan resep terbatas
                    recipeListAdapter.updateData(limitedRecipes)
                } else {
                    showToast("Failed to generate recipes: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<RecipeResponse>>, t: Throwable) {
                binding.progressBarRecipe.visibility = View.GONE // Sembunyikan ProgressBar
                showToast("Error: ${t.message ?: "Unknown error"}")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
