package com.example.gymkhana

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NutritionApiService {
    @GET("food/ingredients/search")
    suspend fun searchFood(@Query("query") food: String): Response<FoodSearchResponse>

    @GET("recipes/{id}/nutritionWidget.json")
    suspend fun getNutritionalInfo(@Path("id") recipeId: Int): Response<NutritionalData>
}