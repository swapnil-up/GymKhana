package com.example.gymkhana

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        // Find views
        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        val previousMonthButton: Button = findViewById(R.id.previousMonthButton)
        val nextMonthButton: Button = findViewById(R.id.nextMonthButton)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)

        // Set up the RecyclerView with the calendar adapter
        calendarAdapter = CalendarAdapter(generateDatesForMonth())
        calendarRecyclerView.adapter = calendarAdapter
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        // Set month-year text
        val currentMonthYear = getCurrentMonthYear()
        monthYearTextView.text = currentMonthYear

        // Set click listeners for previous and next month buttons
        previousMonthButton.setOnClickListener {
            navigateToPreviousMonth()
        }

        nextMonthButton.setOnClickListener {
            navigateToNextMonth()
        }
    }

    private fun generateDatesForMonth(): List<Date> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DATE, -1)
        val endDate = calendar.time

        calendar.time = startDate

        while (!calendar.time.after(endDate)) {
            dates.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        return dates
    }
    private fun getCurrentMonthYear(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun navigateToNextMonth() {
        // Update the list of dates and notify the adapter
        val dates = generateDatesForNextMonth()
        calendarAdapter.updateDates(dates)
        calendarAdapter.notifyDataSetChanged()

        // Update the month-year text
        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        val currentMonthYear = getCurrentMonthYear()
        monthYearTextView.text = currentMonthYear
    }

    private fun generateDatesForNextMonth(): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        return generateDatesForMonth(calendar)
    }


    private fun navigateToPreviousMonth() {
        // Update the list of dates and notify the adapter
        val dates = generateDatesForPreviousMonth()
        calendarAdapter.updateDates(dates)
        calendarAdapter.notifyDataSetChanged()

        // Update the month-year text
        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        val currentMonthYear = getCurrentMonthYear()
        monthYearTextView.text = currentMonthYear
    }

    private fun generateDatesForPreviousMonth(): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        return generateDatesForMonth()
    }

}
