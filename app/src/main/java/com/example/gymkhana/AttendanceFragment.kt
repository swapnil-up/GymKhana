package com.example.gymkhana

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar
import java.util.Date

class AttendanceFragment : Fragment() {

    private lateinit var recyclerView:RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView= inflater.inflate(R.layout.fragment_attendance, container, false)
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Perform any additional setup or UI initialization here
        //attendance segment


        val year = 2023 // Provide the desired year
        val month = Calendar.JUNE // Provide the desired month (Calendar.JANUARY = 0, Calendar.FEBRUARY = 1, etc.)

        val dates = generateMonthDates(year, month)
        val adapter = CalendarAdapter(dates)
        recyclerView.adapter = adapter
    }

    private fun generateMonthDates(year: Int, month: Int): List<Date> {
        // Implement the logic to generate the list of dates for the specified month and year
        // Here's an example implementation using the java.util.Calendar class:

        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1) // Set the calendar to the first day of the specified month and year

        val dates = mutableListOf<Date>()

        while (calendar.get(Calendar.MONTH) == month) {
            val date = calendar.time
            dates.add(date)
            calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
        }

        return dates
    }

}