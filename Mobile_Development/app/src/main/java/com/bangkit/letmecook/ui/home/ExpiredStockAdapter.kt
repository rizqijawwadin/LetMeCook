package com.bangkit.letmecook.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ExpiredStockAdapter : RecyclerView.Adapter<ExpiredStockAdapter.StockViewHolder>() {
    private val stocks = mutableListOf<InventoryEntity>()
    private val daysLeftLabels = mutableListOf<String>()

    fun submitList(newStocks: List<InventoryEntity>) {
        stocks.clear()
        stocks.addAll(newStocks)
        notifyDataSetChanged()
    }

    fun setDaysLeftLabels(labels: List<String>) {
        daysLeftLabels.clear()
        daysLeftLabels.addAll(labels)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_horizontal, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stocks[position]
        val label = daysLeftLabels.getOrNull(position) ?: ""

        holder.bind(stock, label)
    }

    override fun getItemCount(): Int = stocks.size

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val stockNameTextView: TextView = itemView.findViewById(R.id.titleStock)
        private val daysLeftTextView: TextView = itemView.findViewById(R.id.expiredDate)
        private val stockImageView: ImageView = itemView.findViewById(R.id.imageStock)

        fun bind(stock: InventoryEntity, daysLeftLabel: String) {
            stockNameTextView.text = stock.ingredient_name
            daysLeftTextView.text = daysLeftLabel

            Glide.with(itemView.context)
                .load(stock.ingredients_pic)  // Assuming stock has an 'imageUrl' field
                .apply(RequestOptions().centerCrop()) // Placeholder image
                .into(stockImageView)
        }
    }
}
