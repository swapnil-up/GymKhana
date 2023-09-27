package com.example.gymkhanaadmin.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhanaadmin.R

class ClassesAdapter(private var classesList: List<GymClass>) : RecyclerView.Adapter<ClassesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val classNameTextView: TextView = itemView.findViewById(R.id.className)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gymClass = classesList[position]
        holder.classNameTextView.text = gymClass.className
    }

    override fun getItemCount(): Int {
        return classesList.size
    }

    fun updateData(newClassesList: List<GymClass>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }
}