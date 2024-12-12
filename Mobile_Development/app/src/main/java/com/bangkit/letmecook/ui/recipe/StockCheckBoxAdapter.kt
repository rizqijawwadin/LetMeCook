package com.bangkit.letmecook.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.local.entity.InventoryEntity

class CheckboxAdapter(
    private val items: List<InventoryEntity>,
    private val onItemChecked: (InventoryEntity, Boolean) -> Unit
) : RecyclerView.Adapter<CheckboxAdapter.CheckboxViewHolder>() {

    private val checkedItems = mutableSetOf<InventoryEntity>()

    class CheckboxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.item_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckboxViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ingredients, parent, false)
        return CheckboxViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckboxViewHolder, position: Int) {
        val item = items[position]
        holder.checkbox.text = item.ingredient_name
        holder.checkbox.isChecked = checkedItems.contains(item)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedItems.add(item)
            } else {
                checkedItems.remove(item)
            }
            onItemChecked(item, isChecked)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getCheckedItems(): List<InventoryEntity> = checkedItems.toList()
}
