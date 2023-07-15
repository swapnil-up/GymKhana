package com.example.gymkhana

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarAdapter(
    private val context: Context,
    private val currentDate: Calendar,
    private val attendanceRef: DatabaseReference,
    private val onUpdate: (totalDays: Int, streakLength: Int) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private var dates: List<Date> = generateDatesForMonth(currentDate)
    private var attendanceData: MutableMap<String, Int> = mutableMapOf()
    private var userId: String = ""

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.cellDayText)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val date = dates[position]
                    toggleDateSelection(date)
                    updateTotalDaysAndStreak()
                    itemView.invalidate() // Update the item view to reflect the changes
                }
            }
        }
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

        val dateString = getDateString(date)
        val attendanceStatus = attendanceData[dateString]

        val colorResId = when (attendanceStatus) {
            1 -> R.drawable.bg_green_color // Green (present)
            2 -> R.drawable.bg_red_color // Red (absent)
            else -> R.color.secondary // Default color
        }

        holder.dateTextView.setBackgroundResource(colorResId)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    private fun generateDatesForMonth(calendar: Calendar): List<Date> {
        val dates = mutableListOf<Date>()

        val startCalendar = calendar.clone() as Calendar
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val endCalendar = calendar.clone() as Calendar
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        while (!startCalendar.after(endCalendar)) {
            dates.add(startCalendar.time)
            startCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    fun updateMonthYear(monthYear: String) {
        currentDate.time = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(monthYear) ?: Date()
        dates = generateDatesForMonth(currentDate)
        notifyDataSetChanged()
    }

    fun fetchAttendanceData(userId: String) {
        attendanceRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                attendanceData.clear() // Clear the existing data before updating
                for (dateSnapshot in snapshot.children) {
                    val dateString = dateSnapshot.key
                    val attendance = dateSnapshot.getValue(Int::class.java)
                    if (dateString != null && attendance != null) {
                        attendanceData[dateString] = attendance
                    }
                }
                notifyDataSetChanged()
                updateTotalDaysAndStreak()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun saveAttendanceData(userId: String) {
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val attendanceRef = database.getReference("attendanceGUI")

        attendanceRef.child(userId).setValue(attendanceData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data saved successfully
                } else {
                    // Handle error
                }
            }
    }



    private fun toggleDateSelection(date: Date) {
        val dateString = getDateString(date)
        val attendanceStatus = attendanceData[dateString]

        when (attendanceStatus) {
            null -> attendanceData[dateString] = 1 // Green (present) on first tap
            1 -> attendanceData[dateString] = 2 // Red (absent) on second tap
            else -> attendanceData.remove(dateString) // Remove on third tap (back to default)
        }

        notifyDataSetChanged()
    }

    private fun updateTotalDaysAndStreak() {
        val totalDays = attendanceData.count { it.value == 1 } // Count the days with attendance status 1 (green)
        val streakLength = calculateStreakLength()
        onUpdate(totalDays, streakLength)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        saveAttendanceData(userId) // Save the updated attendance data
    }


    private fun calculateStreakLength(): Int {
        var currentStreak = 0
        var longestStreak = 0

        val sortedDates = attendanceData.keys.sorted()
        for (i in 0 until sortedDates.size) {
            val currentDate = sortedDates[i]
            if (attendanceData[currentDate] == 1) { // Check if attendance status is 1 (green)
                currentStreak++
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak
                }
            } else {
                currentStreak = 0
            }
        }

        return longestStreak
    }


    private fun getDateString(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
}