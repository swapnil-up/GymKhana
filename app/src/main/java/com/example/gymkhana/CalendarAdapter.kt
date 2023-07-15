package com.example.gymkhana

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymkhana.AttendanceActivity
import java.util.*

class CalendarAdapter(
    private val context: Context,
    private var dates: List<Date>,
    private val highlightedDates: MutableMap<Date, Int>
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.cellDayText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = dates[position]
        val calendar = Calendar.getInstance()
        calendar.time = date

        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        holder.dateTextView.text = dayOfMonth.toString()

        val clickCount = highlightedDates[date] ?: 0

        when (clickCount) {
            0 -> holder.dateTextView.setBackgroundResource(R.drawable.bg_no_color) // No color
            1 -> holder.dateTextView.setBackgroundResource(R.drawable.bg_green_color) // Green color
            2 -> holder.dateTextView.setBackgroundResource(R.drawable.bg_red_color) // Red color
        }

        holder.itemView.setOnClickListener {
            val newClickCount = (clickCount + 1) % 3
            highlightedDates[date] = newClickCount
            notifyDataSetChanged() // Update the adapter to reflect the changes
            updateTotalDaysAndStreak()
        }
    }
    override fun getItemCount(): Int {
        return dates.size
    }

    private fun toggleDateSelection(date: Date) {
        if (highlightedDates.containsKey(date)) {
            highlightedDates.remove(date)
        } else {
            highlightedDates[date] = 1
        }
    }

    fun updateDates(newDates: List<Date>) {
        dates = newDates
        notifyDataSetChanged()
    }

    private fun updateTotalDaysAndStreak() {
        val totalDays = highlightedDates.count{it.value==1}
        val streakLength = calculateStreakLength()
        (context as AttendanceActivity).updateTotalDays(totalDays)
        (context as AttendanceActivity).updateStreakLength(streakLength)
    }

    private fun calculateStreakLength(): Int {
        var streakLength = 0
        var currentStreak = 0

        val sortedDates = highlightedDates.keys.sorted()
        val calendar = Calendar.getInstance()

        for (date in sortedDates) {
            calendar.time = date
            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

            if (highlightedDates.containsKey(date)) {
                if (currentStreak == 0 || dayOfYear == currentStreak + 1) {
                    currentStreak++
                } else {
                    currentStreak = 1
                }

                if (currentStreak > streakLength) {
                    streakLength = currentStreak
                }
            }
        }

        return streakLength
    }
}