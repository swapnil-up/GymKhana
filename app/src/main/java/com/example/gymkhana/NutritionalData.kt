package com.example.gymkhana

data class NutritionalData(
    val nutrients: List<Nutrient>,
    /*val properties: List<Property>,
    val flavonoids: List<Flavonoid>,
    val ingredients: List<Ingredient>,*/
    val caloricBreakdown: CaloricBreakdown,
    val weightPerServing: WeightPerServing
)

data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String,
    val percentOfDailyNeeds: Double
)

data class Property(
    val name: String,
    val amount: Double,
    val unit: String
)

data class Flavonoid(
    val name: String,
    val amount: Double,
    val unit: String
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val nutrients: List<Nutrient>
)

data class CaloricBreakdown(
    val percentProtein: Double,
    val percentFat: Double,
    val percentCarbs: Double
)

data class WeightPerServing(
    val amount: Int,
    val unit: String
)

