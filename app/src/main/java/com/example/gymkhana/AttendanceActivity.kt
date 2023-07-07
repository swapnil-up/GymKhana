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
    private lateinit var currentDate: Calendar
    private lateinit var totalDaysTextView: TextView
    private lateinit var streakLengthTextView: TextView
    private val highlightedDates = mutableMapOf<Date, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        val previousMonthButton: Button = findViewById(R.id.previousMonthButton)
        val nextMonthButton: Button = findViewById(R.id.nextMonthButton)
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        totalDaysTextView = findViewById(R.id.totalDaysTextView)
        streakLengthTextView = findViewById(R.id.streakLengthTextView)

        currentDate = Calendar.getInstance()
        val currentMonthYear = getCurrentMonthYear(currentDate)
        monthYearTextView.text = currentMonthYear

        calendarAdapter = CalendarAdapter(this, generateDatesForMonth(currentDate), highlightedDates)
        calendarRecyclerView.adapter = calendarAdapter
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        previousMonthButton.setOnClickListener {
            navigateToPreviousMonth()
        }

        nextMonthButton.setOnClickListener {
            navigateToNextMonth()
        }

        updateStreakFeatures()
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

    private fun getCurrentMonthYear(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun navigateToNextMonth() {
        currentDate.add(Calendar.MONTH, 1)
        updateCalendar()
        updateStreakFeatures()
    }

    private fun navigateToPreviousMonth() {
        currentDate.add(Calendar.MONTH, -1)
        updateCalendar()
        updateStreakFeatures()
    }

    private fun updateCalendar() {
        val dates = generateDatesForMonth(currentDate)
        calendarAdapter.updateDates(dates)
        calendarAdapter.notifyDataSetChanged()

        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        val currentMonthYear = getCurrentMonthYear(currentDate)
        monthYearTextView.text = currentMonthYear
    }

    private fun updateStreakFeatures() {
        val dates = generateDatesForMonth(currentDate)
        val totalDays = calculateTotalDaysPresent(dates)
        val streakLength = calculateStreakLength(dates)
        updateTotalDays(totalDays)
        updateStreakLength(streakLength)
    }

    fun updateTotalDays(totalDays: Int) {
        totalDaysTextView.text = "Total Days: $totalDays"
    }

    fun updateStreakLength(streakLength: Int) {
        streakLengthTextView.text = "Streak Length: $streakLength"
    }

    private fun calculateTotalDaysPresent(dates: List<Date>): Int {
        var totalDays = 0
        for (date in dates) {
            if (highlightedDates.containsKey(date)) {
                totalDays++
            }
        }
        return totalDays
    }

    private fun calculateStreakLength(dates: List<Date>): Int {
        var streakLength = 0
        var currentStreak = 0

        for (date in dates) {
            if (highlightedDates.containsKey(date)) {
                currentStreak++
            } else {
                if (currentStreak > streakLength) {
                    streakLength = currentStreak
                }
                currentStreak = 0
            }
        }

        if (currentStreak > streakLength) {
            streakLength = currentStreak
        }

        return streakLength
    }
}
