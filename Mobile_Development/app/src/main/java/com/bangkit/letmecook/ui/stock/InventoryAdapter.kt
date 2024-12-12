package com.bangkit.letmecook.ui.stock

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.letmecook.R
import com.bangkit.letmecook.local.entity.InventoryEntity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.makeramen.roundedimageview.RoundedImageView
import java.util.Locale
import java.util.concurrent.TimeUnit

class InventoryAdapter(
    private val onEditClicked: (InventoryEntity) -> Unit,
    private val onDeleteClicked: (Int) -> Unit
) : ListAdapter<InventoryEntity, InventoryAdapter.InventoryViewHolder>(DiffCallback) {

    class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockPhoto: RoundedImageView = itemView.findViewById(R.id.item_stock_photo)
        val stockName: TextView = itemView.findViewById(R.id.item_stock_name)
        val stockAmount: TextView = itemView.findViewById(R.id.item_stock_amount)
        val stockExpired: TextView = itemView.findViewById(R.id.item_stock_expired)
        val editButton: ImageButton = itemView.findViewById(R.id.item_stock_edit)
        val deleteButton: MaterialCardView = itemView.findViewById(R.id.item_stock_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return InventoryViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = getItem(position)
        val expiryDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.expiry_date)
        val currentDate = Calendar.getInstance()
        val diffInMillis = expiryDate.time - currentDate.timeInMillis
        val diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)

        holder.stockName.text = item.ingredient_name
        holder.stockAmount.text = "${item.stock}"
        holder.stockExpired.text = if (diffInDays > 0) {
            "Expires in $diffInDays days"
        } else {
            "Expired"
        }

        Glide.with(holder.itemView.context)
            .load(item.ingredients_pic)
            .apply(RequestOptions().centerCrop())
            .into(holder.stockPhoto)

        holder.editButton.setOnClickListener {
            onEditClicked(item)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(item.id_inventory)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<InventoryEntity>() {
            override fun areItemsTheSame(oldItem: InventoryEntity, newItem: InventoryEntity): Boolean {
                return oldItem.id_inventory == newItem.id_inventory
            }

            override fun areContentsTheSame(oldItem: InventoryEntity, newItem: InventoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}