package com.example.gymkhana

import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder.ViewHolder>(){
    class CalendarAdapter(private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
        private val daysOfMonth = mutableListOf<CalendarDay>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val day = daysOfMonth[position]
            holder.dayTextView.text = day.day.toString()

            // Set the background color based on the attendance state
            if (day.isPresent) {
                holder.dayTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            } else {
                holder.dayTextView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }

            // Handle click events to toggle the attendance state
            holder.dayTextView.setOnClickListener {
                day.toggleAttendance()
                notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return daysOfMonth.size
        }

        fun setDaysOfMonth(days: List<CalendarDay>) {
            daysOfMonth.clear()
            daysOfMonth.addAll(days)
            notifyDataSetChanged()
        }
    }

}