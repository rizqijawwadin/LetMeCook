package com.bangkit.letmecook.ui.stock

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.local.entity.InventoryEntity
import androidx.recyclerview.widget.DiffUtil


class IngredientsAdapter(
    private val ingredients: List<String>,
    private val onIngredientSelected: (String) -> Unit
) : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    private val selectedIngredients = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredients, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    inner class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.item_checkbox)

        fun bind(ingredient: String) {
            checkBox.text = ingredient
            checkBox.isChecked = selectedIngredients.contains(ingredient)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedIngredients.add(ingredient)
                } else {
                    selectedIngredients.remove(ingredient)
                }
                onIngredientSelected(ingredient)
            }
        }
    }
}
