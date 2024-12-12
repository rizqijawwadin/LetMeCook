package com.bangkit.letmecook.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R

class IngredientAdapter(
    private val onCheckedChange: (String, Boolean) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.ViewHolder>() {

    private val ingredients = mutableListOf<String>()

    fun submitList(list: List<String>) {
        ingredients.clear()
        ingredients.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int = ingredients.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_ingredient)

        fun bind(ingredient: String) {
            checkBox.text = ingredient
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(ingredient, isChecked)
            }
        }
    }
}
