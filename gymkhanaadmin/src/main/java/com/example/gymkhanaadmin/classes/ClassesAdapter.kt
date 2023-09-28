package com.example.gymkhanaadmin.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhanaadmin.R
import com.squareup.picasso.Picasso

class ClassesAdapter(private var classesList: List<GymClass>) : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.className)
        val classDescription: TextView = itemView.findViewById(R.id.classDescription)
        val classImageView: ImageView = itemView.findViewById(R.id.classImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classesList[position]
        holder.classNameTextView.text = gymClass.className
        holder.classDescription.text = gymClass.classDesc

        // Load the image from the URL using Picasso
         Picasso.get()
            .load(gymClass.imageUrl)
            .placeholder(R.drawable.notification) // Placeholder image
            .error(R.drawable.bg_no_color) // Error image (optional)
            .into(holder.classImageView)
    }

    override fun getItemCount(): Int {
        return classesList.size
    }

    fun updateData(newClassesList: List<GymClass>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }
}