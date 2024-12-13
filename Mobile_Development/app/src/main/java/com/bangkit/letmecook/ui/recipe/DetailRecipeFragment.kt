package com.bangkit.letmecook.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bangkit.letmecook.data.response.RecipeDetail
import com.bangkit.letmecook.data.response.RecipeResponse
import com.bangkit.letmecook.data.retrofit.ApiConfig
import com.bangkit.letmecook.databinding.FragmentDetailRecipeBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailRecipeFragment : Fragment() {

    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRecipeBinding.inflate(inflater, container, false)

        val args = DetailRecipeFragmentArgs.fromBundle(requireArguments())
        val recipeId = args.recipeId

        fetchRecipeDetails(recipeId)

        binding.btnLetMeCook.setOnClickListener {
            showToast("Cooked!")
        }


        return binding.root
    }

    private fun fetchRecipeDetails(recipeId: Int) {
        binding.progressBarDetailRecipes.visibility = View.VISIBLE
        ApiConfig.getApiService().getRecipeDetails(recipeId)
            .enqueue(object : Callback<RecipeDetail> {
                override fun onResponse(
                    call: Call<RecipeDetail>,

                    response: Response<RecipeDetail>

                ) {
                    binding.progressBarDetailRecipes.visibility = View.GONE
                    if (response.isSuccessful) {
                        val recipeDetail = response.body()
                        if (recipeDetail != null) {
                            Log.d("DetailRecipe", "Recipe Detail: $recipeDetail")
                            bindRecipeDetails(recipeDetail)
                        } else {
                            showToast("Recipe details not found")
                        }
                    } else {
                        showToast("Failed to fetch details: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<RecipeDetail>, t: Throwable) {
                    binding.progressBarDetailRecipes.visibility = View.GONE
                    showToast("Error: ${t.message}")
                }
            })
    }

    private fun bindRecipeDetails(recipe: RecipeDetail) {
        binding.recipeName.text = recipe.nameRecipe
        binding.cookingTime.text = "Cooking Time: ${recipe.prepTime}"
        binding.serves.text = "Serves: ${recipe.serves}"
        binding.ingredients.text = "ingredients: ${recipe.ingredients}"
        binding.tvCookingSteps.text = "Cooking Method: ${recipe.cookingMethod}"

        Glide.with(this)
            .load(recipe.image)
            .placeholder(android.R.drawable.progress_indeterminate_horizontal)
            .error(android.R.drawable.stat_notify_error)
            .into(binding.recipeImage)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
