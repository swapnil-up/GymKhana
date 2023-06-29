package com.example.gymkhana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MealPlanActivity : AppCompatActivity() {

    private val API_BASE_URL = "https://api.spoonacular.com/"
    private val API_KEY = "c9735e4db52b42808388ca6aa81f728b"

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val mealPlannerApi = retrofit.create(MealPlannerApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_plan)

        val timeFrameEditText: EditText = findViewById(R.id.timeFrameEditText)
        val targetCaloriesEditText: EditText = findViewById(R.id.targetCaloriesEditText)
        val dietEditText: EditText = findViewById(R.id.dietEditText)
        val generateButton: Button = findViewById(R.id.generateButton)


        // Call the generateMealPlan method to request and display the meal plan
        generateButton.setOnClickListener {
            val timeFrame = timeFrameEditText.text.toString()
            val targetCalories = targetCaloriesEditText.text.toString().toInt()
            val diet = dietEditText.text.toString()

            generateMealPlan(timeFrame, targetCalories, diet)
        }
    }


    private fun generateMealPlan(timeFrame: String, targetCalories: Int, diet: String) {
        val call = mealPlannerApi.generateMealPlan(timeFrame, targetCalories, diet, API_KEY)
        call.enqueue(object : Callback<MealPlanResponse> {
            override fun onResponse(
                call: Call<MealPlanResponse>,
                response: Response<MealPlanResponse>
            ) {
                if (response.isSuccessful) {
                    val mealPlanResponse = response.body()
                    val meals = mealPlanResponse?.meals
                    val nutrients = mealPlanResponse?.nutrients
                    displayMealPlan(meals, nutrients)
                } else {
                    // Display an error message when API request fails
                    Toast.makeText(
                        this@MealPlanActivity,
                        "Failed to generate meal plan. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MealPlanResponse>, t: Throwable) {
                // Display an error message when the API request encounters an error
                Toast.makeText(
                    this@MealPlanActivity,
                    "Failed to generate meal plan. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun displayMealPlan(meals: List<Meal>?, nutrients: Nutrients?) {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView_meals)

        if (meals != null && meals.isNotEmpty() && nutrients != null) {
            val mealAdapter = MealAdapter(meals, nutrients)
            recyclerView.adapter = mealAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        } else {
            // Display a message when there are no meals or nutrients
            Toast.makeText(this, "No meals found.", Toast.LENGTH_SHORT).show()
        }
    }

}