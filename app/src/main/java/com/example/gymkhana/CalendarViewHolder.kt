package com.example.gymkhana

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date

class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val dateTextView: TextView = itemView.findViewById(R.id.cellDayText)

    fun bind(date: Date) {
        // Customize the binding logic here
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Example: Set the day number
        dateTextView.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
    }
}
