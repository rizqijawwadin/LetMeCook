package com.bangkit.letmecook.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.local.entity.ResponseMLItem

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private val recipes = mutableListOf<ResponseMLItem?>()

    fun submitList(list: List<ResponseMLItem?>) {
        recipes.clear()
        recipes.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_recipes, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int = recipes.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_recipe_name)

        fun bind(recipe: ResponseMLItem?) {
            nameTextView.text = recipe?.nameRecipe
        }
    }
}
