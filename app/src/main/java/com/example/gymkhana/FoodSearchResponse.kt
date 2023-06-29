package com.example.gymkhana

data class FoodSearchResponse(
    val results: List<FoodItem>
)

data class FoodItem(
    val id: Int,
    val title: String,
    val imageType:String,
    val readyInMinutes:Int,
    val service:Int,
    val sourceUrl:String
    // Add other relevant properties here based on the Spoonacular API response
)
