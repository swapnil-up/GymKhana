package com.example.gymkhana

import java.util.*

   object DateUtils {
        fun generateMonthDates(year: Int, month: Int): List<Date> {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1) // Set the desired year and month

            val dates = mutableListOf<Date>()

            // Get the number of days in the month
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            // Generate dates for each day of the month
            for (day in 1..daysInMonth) {
                val date = calendar.time
                dates.add(date)

                calendar.add(Calendar.DAY_OF_MONTH, 1) // Move to the next day
            }

            return dates
        }
    }