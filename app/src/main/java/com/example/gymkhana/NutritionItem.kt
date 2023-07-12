package com.example.gymkhana

data class NutritionItem(val id: Int, val name: String) {
    companion object {
        val nutritionList = arrayListOf(
            NutritionItem(1002002,"5 spice powder"),
            NutritionItem(11482, "Acorn squash"),
            NutritionItem(6979, "Adobo sauce"),
            NutritionItem(19912, "Agave nectar"),
            NutritionItem(15117, "Ahi tuna"),
            NutritionItem(93606, "Alfredo pasta sauce"),
            NutritionItem(1002050, "Almond extract"),
            NutritionItem(93740, "Almond flour"),
            NutritionItem(93607, "Almond milk"),
            NutritionItem(12061, "Almonds")
        )

    }
}