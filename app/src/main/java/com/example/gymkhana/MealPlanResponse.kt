package com.example.gymkhana

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class MealPlanResponse(
    val meals: List<Meal>?,
    val nutrients: Nutrients?
)

data class Meal(
    val id: Int,
    val title: String,
    val imageType: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String,
    val nutrition: Nutrients?
)

data class Nutrients(
    val calories: Double,
    val carbohydrates: Double,
    val fat: Double,
    val protein: Double
)

interface MealPlannerApi {
    @GET("mealplanner/generate")
    fun generateMealPlan(
        @Query("timeFrame") timeFrame: String,
        @Query("targetCalories") targetCalories: Int,
        @Query("diet") diet: String,
        @Query("apiKey") apiKey: String
    ): Call<MealPlanResponse>
}
