package com.example.gymkhana.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhana.GymClass
import com.example.gymkhana.R
import com.squareup.picasso.Picasso

class ClassesAdapter(
    private var classesList: List<GymClass>,
    private val onJoinClick: (Int) -> Unit,
    private val onLeaveClick: (Int) -> Unit
) : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.className)
        val classDescription: TextView = itemView.findViewById(R.id.classDescription)
        val classImageView: ImageView = itemView.findViewById(R.id.classImage)
        val btnJoin: Button = itemView.findViewById(R.id.btnJoin)
        val btnLeave: Button = itemView.findViewById(R.id.btnLeave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classesList[position]
        holder.classNameTextView.text = gymClass.className
        holder.classDescription.text = gymClass.classDesc
        Picasso.get()
            .load(gymClass.imageUrl)
            .placeholder(R.drawable.notification) // Placeholder image
            .error(R.drawable.bg_no_color) // Error image (optional)
            .into(holder.classImageView)

        // Set click listeners for the "Join" and "Leave" buttons
        holder.btnJoin.setOnClickListener { onJoinClick(position) }
        holder.btnLeave.setOnClickListener { onLeaveClick(position) }
    }

    override fun getItemCount(): Int {
        return classesList.size
    }

    fun updateData(newClassesList: List<GymClass>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }

    fun onDataChanged(newClassesList: List<GymClass>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }
}
