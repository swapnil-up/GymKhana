package com.example.gymkhana.classes

import android.util.Log
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
    private val onJoinClick: (String) -> Unit,
    private val onLeaveClick: (String) -> Unit,
    private var userJoinedClasses: List<String> = emptyList()
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
        val classId = gymClass.classId // Get the classId
        holder.classNameTextView.text = gymClass.className
        holder.classDescription.text = gymClass.classDesc
        Picasso.get()
            .load(gymClass.imageUrl)
            .placeholder(R.drawable.notification)
            .error(R.drawable.bg_no_color)
            .into(holder.classImageView)

        // Check if the user has already joined this class and update button visibility accordingly
        if (userJoinedClasses.contains(classId)) {
            holder.btnJoin.visibility = View.GONE
            holder.btnLeave.visibility = View.VISIBLE
        } else {
            holder.btnJoin.visibility = View.VISIBLE
            holder.btnLeave.visibility = View.GONE
        }

        // Set click listeners for the buttons
        holder.btnJoin.setOnClickListener {
            onJoinClick(classId)
            holder.btnJoin.visibility = View.GONE
            holder.btnLeave.visibility = View.VISIBLE
        }
        holder.btnLeave.setOnClickListener {
            onLeaveClick(classId)
            holder.btnJoin.visibility = View.VISIBLE
            holder.btnLeave.visibility = View.GONE
        }

        for (gymClass in classesList) {
            Log.d("GymClass", "Class ID: ${gymClass.classId}, Class Name: ${gymClass.className}")
        }
    }

    override fun getItemCount(): Int {
        return classesList.size
    }


    fun onDataChanged(newClassesList: List<GymClass>) {
        classesList = newClassesList
        notifyDataSetChanged()
    }

    // Function to update the user's joined classes
    fun updateUserJoinedClasses(userJoinedClasses: List<String>) {
        this.userJoinedClasses = userJoinedClasses
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }


}
