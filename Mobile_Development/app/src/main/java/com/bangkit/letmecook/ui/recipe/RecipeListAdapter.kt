package com.bangkit.letmecook.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.data.response.RecipeResponse
import com.bangkit.letmecook.databinding.CardItemRecipesBinding
import com.bumptech.glide.Glide

class RecipeListAdapter(private var recipes: List<RecipeResponse>) :
    RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {

    // Update data and notify RecyclerView about the data change
    fun updateData(newRecipes: List<RecipeResponse>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemRecipesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    // ViewHolder class to bind the data to the views
    class ViewHolder(private val binding: CardItemRecipesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecipeResponse) {
            // Binding the recipe name
            binding.recipeName.text = recipe.name_recipe

            // Set categories (assuming categories is a list of strings)
            val categoriesText = recipe.categories.joinToString(", ")
            binding.categoryTag.text = categoriesText

            // Load the recipe image using Glide
            Glide.with(binding.recipeImage.context)
                .load(recipe.image) // Pass the image URL
                .placeholder(android.R.drawable.progress_indeterminate_horizontal) // Placeholder while loading
                .error(android.R.drawable.stat_notify_error) // Error image if loading fails
                .into(binding.recipeImage) // Target imageView (RoundedImageView in your case)
        }
    }
}
