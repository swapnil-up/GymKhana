package com.example.gymkhanaadmin.classes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhanaadmin.R

class JoinedClassesAdapter(private val joinedClassesList: MutableList<JoinedClass>) :
    RecyclerView.Adapter<JoinedClassesAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val classNameTextView: TextView = itemView.findViewById(R.id.classNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.joined_class_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val joinedClass = joinedClassesList[position]
        holder.userNameTextView.text = joinedClass.userId
        holder.classNameTextView.text = joinedClass.classId // You can change this to the appropriate property of JoinedClass
    }

    override fun getItemCount(): Int {
        return joinedClassesList.size
    }

    fun updateData(newJoinedClassesList: List<JoinedClass>) {
        joinedClassesList.clear()
        joinedClassesList.addAll(newJoinedClassesList)
        notifyDataSetChanged()
    }
}
