package com.example.gymkhana

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MealAdapter(
    private val meals: List<Meal>,
    private val nutrients: Nutrients
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MEAL = 0
    private val VIEW_TYPE_NUTRIENTS = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MEAL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_meal, parent, false)
                MealViewHolder(view)
            }
            VIEW_TYPE_NUTRIENTS -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_nutrients, parent, false)
                NutrientsViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_MEAL -> {
                val mealHolder = holder as MealViewHolder
                val meal = meals[position]
                mealHolder.bind(meal)
            }
            VIEW_TYPE_NUTRIENTS -> {
                val nutrientsHolder = holder as NutrientsViewHolder
                nutrientsHolder.bind(nutrients)
            }
        }
    }

    override fun getItemCount(): Int {
        // Add 1 to the count to include the nutrients view holder
        return meals.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < meals.size) VIEW_TYPE_MEAL else VIEW_TYPE_NUTRIENTS
    }

    inner class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textView_title)
        private val readyInMinutesTextView: TextView = itemView.findViewById(R.id.textView_readyInMinutes)
        private val servingsTextView: TextView = itemView.findViewById(R.id.textView_servings)
        private val sourceUrlTextView: TextView = itemView.findViewById(R.id.textView_sourceUrl)
      //  private val caloriesTextView: TextView = itemView.findViewById(R.id.textView_calories)
      //  private val carbohydratesTextView: TextView = itemView.findViewById(R.id.textView_carbohydrates)
      //  private val fatTextView: TextView = itemView.findViewById(R.id.textView_fat)
     //   private val proteinTextView: TextView = itemView.findViewById(R.id.textView_protein)

        fun bind(meal: Meal) {
            titleTextView.text = meal.title
            readyInMinutesTextView.text = itemView.context.getString(R.string.ready_in_minutes, meal.readyInMinutes)
            servingsTextView.text = itemView.context.getString(R.string.servings, meal.servings)
            sourceUrlTextView.text = meal.sourceUrl
//            caloriesTextView.text = itemView.context.getString(R.string.calories, meal.nutrition?.calories)
//            carbohydratesTextView.text = itemView.context.getString(R.string.carbohydrates, meal.nutrition?.carbohydrates)
//            fatTextView.text = itemView.context.getString(R.string.fat, meal.nutrition?.fat)
//            proteinTextView.text = itemView.context.getString(R.string.protein, meal.nutrition?.protein)
        }
    }

    inner class NutrientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val caloriesTextView: TextView = itemView.findViewById(R.id.textView_calories)
        private val carbsTextView: TextView = itemView.findViewById(R.id.textView_carbs)
        private val fatTextView: TextView = itemView.findViewById(R.id.textView_fat)
        private val proteinTextView: TextView = itemView.findViewById(R.id.textView_protein)

        fun bind(nutrients: Nutrients) {
            caloriesTextView.text = itemView.context.getString(R.string.calories, nutrients.calories)
            carbsTextView.text = itemView.context.getString(R.string.carbs, nutrients.carbohydrates)
            fatTextView.text = itemView.context.getString(R.string.fat, nutrients.fat)
            proteinTextView.text = itemView.context.getString(R.string.protein, nutrients.protein)
        }
    }
}
