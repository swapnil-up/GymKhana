package com.example.gymkhana

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class StoreAdapter(private var itemList: List<StoreItem>) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.store_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    fun updateItemList(newItemList: List<StoreItem>) {
        itemList = newItemList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImageView: ImageView = itemView.findViewById(R.id.imageViewItem)
        private val itemNameTextView: TextView = itemView.findViewById(R.id.textViewItemName)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.textViewItemPrice)

        fun bind(item: StoreItem) {
            // Set item data to the views
            Glide.with(itemView)
                .load(item.imageURL)
                .into(itemImageView)

            itemNameTextView.text = item.name
            itemPriceTextView.text = "Price: $${item.price}"
        }
    }
}

