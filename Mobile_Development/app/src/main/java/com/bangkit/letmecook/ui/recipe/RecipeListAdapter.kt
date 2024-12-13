package com.bangkit.letmecook.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.data.response.RecipeResponse
import com.bangkit.letmecook.databinding.CardItemRecipesBinding
import com.bumptech.glide.Glide

class RecipeListAdapter(
    private var recipes: List<RecipeResponse>,
    private val onItemClick: (RecipeResponse) -> Unit
) : RecyclerView.Adapter<RecipeListAdapter.ViewHolder>() {

    // Perbarui data dalam adapter
    fun updateData(newRecipes: List<RecipeResponse>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardItemRecipesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size

    // ViewHolder dengan callback klik
    class ViewHolder(
        private val binding: CardItemRecipesBinding,
        private val onItemClick: (RecipeResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: RecipeResponse) {
            // Bind data resep ke UI
            binding.recipeName.text = recipe.name_recipe

            val categoriesText = recipe.categories.joinToString(", ")
            binding.categoryTag.text = categoriesText

            // Load gambar menggunakan Glide
            Glide.with(binding.recipeImage.context)
                .load(recipe.image)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.recipeImage)

            // Set listener klik untuk item
            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }
}
