package com.example.gymkhana

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AttendanceActivity : AppCompatActivity() {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var currentDate: Calendar
    private lateinit var totalDaysTextView: TextView
    private lateinit var streakLengthTextView: TextView
    private lateinit var userId: String
    private lateinit var attendanceRef: DatabaseReference

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
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        var firestore = FirebaseFirestore.getInstance()
        val database = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/")
        // Initialize Firebase Realtime Database reference
        attendanceRef = FirebaseDatabase.getInstance("https://gymkhana-5560f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("attendanceGUI")
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        // Create and initialize the CalendarAdapter
        calendarAdapter = CalendarAdapter(this, currentDate, attendanceRef) { totalDays, streakLength ->
            updateTotalDaysAndStreak(totalDays, streakLength)
        }

        // Set the adapter on the RecyclerView
        calendarRecyclerView.adapter = calendarAdapter
        calendarRecyclerView.layoutManager = GridLayoutManager(this, 7)

        previousMonthButton.setOnClickListener {
            navigateToPreviousMonth()
        }

        nextMonthButton.setOnClickListener {
            navigateToNextMonth()
        }
        calendarAdapter.fetchAttendanceData(userId)
        updateStreakFeatures()
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
        val currentMonthYear = getCurrentMonthYear(currentDate)
        val monthYearTextView: TextView = findViewById(R.id.monthYearTextView)
        monthYearTextView.text = currentMonthYear
        calendarAdapter.updateMonthYear(currentMonthYear)
        calendarAdapter.notifyDataSetChanged()
    }

    private fun updateTotalDaysAndStreak(totalDays: Int, streakLength: Int) {
        totalDaysTextView.text = "Total Days: $totalDays"
        streakLengthTextView.text = "Streak Length: $streakLength"

        // Replace "userId" with the actual user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        calendarAdapter.saveAttendanceData(userId)
    }



    private fun updateStreakFeatures() {
        // Implement your streak calculation logic based on the attendance data
        // Retrieve the attendance data for the current user and update the streak features
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { userId ->
            attendanceRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendanceData = snapshot.value as? Map<String, Int> ?: emptyMap()
                    val totalDays = attendanceData.size
                    val streakLength = calculateStreakLength(attendanceData)
                    updateTotalDaysAndStreak(totalDays, streakLength)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        calendarAdapter.saveAttendanceData(userId)
    }

    private fun calculateStreakLength(attendanceData: Map<String, Int>): Int {
        // Implement your streak calculation logic based on the attendanceData
        // Return the streak length
        return 0
    }
}
