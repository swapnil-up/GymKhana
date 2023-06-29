package com.example.gymkhana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodSearchActivity : AppCompatActivity() {
    private val apiService: NutritionApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiKey = "c9735e4db52b42808388ca6aa81f728b" // Replace with your actual API key
        retrofit.newBuilder().client(
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newUrl = request.url.newBuilder()
                        .addQueryParameter("apiKey", apiKey)
                        .build()

                    val newRequest = request.newBuilder()
                        .url(newUrl)
                        .build()

                    chain.proceed(newRequest)
                }
                .build()
        ).build().create(NutritionApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)
        val nutritionalInfoEditText: EditText = findViewById(R.id.nutritionalSearchEditText)
        val nutritionButton: Button = findViewById(R.id.NutritionButton)
        val nutritionResultsEditText: EditText = findViewById(R.id.nutritionalResultsEditText)

        nutritionButton.setOnClickListener {
            val food = nutritionalInfoEditText.text.toString()
            launchCoroutine(food, nutritionResultsEditText)
        }
    }

    private fun launchCoroutine(food: String, nutritionResultsEditText: EditText) {
        CoroutineScope(Dispatchers.IO).launch {
            val nutritionalData = fetchNutritionalInfo(food)

            withContext(Dispatchers.Main) {
                updateUI(nutritionalData, nutritionResultsEditText)
            }
        }
    }

    private suspend fun fetchNutritionalInfo(food: String): NutritionalData? {
        val response = apiService.getNutritionalInfo(food.toInt())

        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                val nutrients = responseBody.nutrients.map { nutrient ->
                    Nutrient(
                        nutrient.name,
                        nutrient.amount,
                        nutrient.unit,
                        nutrient.percentOfDailyNeeds
                    )
                }

                /*val properties = responseBody.properties.map { property ->
                    Property(
                        property.name,
                        property.amount,
                        property.unit
                    )
                }

                val flavonoids = responseBody.flavonoids.map { flavonoid ->
                    Flavonoid(
                        flavonoid.name,
                        flavonoid.amount,
                        flavonoid.unit
                    )
                }

                val ingredients = responseBody.ingredients.map { ingredient ->
                    val ingredientNutrients = ingredient.nutrients.map { nutrient ->
                        Nutrient(
                            nutrient.name,
                            nutrient.amount,
                            nutrient.unit,
                            nutrient.percentOfDailyNeeds
                        )
                    }

                    Ingredient(
                        ingredient.id,
                        ingredient.name,
                        ingredient.amount,
                        ingredient.unit,
                        ingredientNutrients
                    )
                }*/

                val caloricBreakdown = CaloricBreakdown(
                    responseBody.caloricBreakdown.percentProtein,
                    responseBody.caloricBreakdown.percentFat,
                    responseBody.caloricBreakdown.percentCarbs
                )

                val weightPerServing = WeightPerServing(
                    responseBody.weightPerServing.amount,
                    responseBody.weightPerServing.unit
                )

                return NutritionalData(
                    nutrients,
                    /*properties,
                    flavonoids,
                    ingredients,*/
                    caloricBreakdown,
                    weightPerServing
                )
            }
        }

        return null
    }

    private fun updateUI(nutritionalData: NutritionalData?, nutritionalInfoTextView: TextView) {
        if (nutritionalData != null) {
            val nutrientsText = buildNutrientsText(nutritionalData.nutrients)
            /*val propertiesText = buildPropertiesText(nutritionalData.properties)
            val flavonoidsText = buildFlavonoidsText(nutritionalData.flavonoids)
            val ingredientsText = buildIngredientsText(nutritionalData.ingredients)*/
            val caloricBreakdownText = buildCaloricBreakdownText(nutritionalData.caloricBreakdown)
            val weightPerServingText = buildWeightPerServingText(nutritionalData.weightPerServing)

            val nutritionalInfoText = StringBuilder().apply {
                append(nutrientsText)
                append("\n\n")
                /*append(propertiesText)
                append("\n\n")
                append(flavonoidsText)
                append("\n\n")
                append(ingredientsText)
                append("\n\n")*/
                append(caloricBreakdownText)
                append("\n\n")
                append(weightPerServingText)
            }.toString()

            nutritionalInfoTextView.text = nutritionalInfoText
        } else {
            // Handle the case when the API request is not successful
            nutritionalInfoTextView.text = "Failed to fetch nutritional information"
        }
    }

    private fun buildNutrientsText(nutrients: List<Nutrient>): String {
        val stringBuilder = StringBuilder("Nutrients:\n")
        for (nutrient in nutrients) {
            val nutrientText = "${nutrient.name}: ${nutrient.amount} ${nutrient.unit}"
            stringBuilder.append(nutrientText).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun buildPropertiesText(properties: List<Property>): String {
        val stringBuilder = StringBuilder("Properties:\n")
        for (property in properties) {
            val propertyText = "${property.name}: ${property.amount} ${property.unit}"
            stringBuilder.append(propertyText).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun buildFlavonoidsText(flavonoids: List<Flavonoid>): String {
        val stringBuilder = StringBuilder("Flavonoids:\n")
        for (flavonoid in flavonoids) {
            val flavonoidText = "${flavonoid.name}: ${flavonoid.amount} ${flavonoid.unit}"
            stringBuilder.append(flavonoidText).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun buildIngredientsText(ingredients: List<Ingredient>): String {
        val stringBuilder = StringBuilder("Ingredients:\n")
        for (ingredient in ingredients) {
            val ingredientText = "${ingredient.name} - ${ingredient.amount} ${ingredient.unit}\n"
            val ingredientNutrientsText = buildNutrientsText(ingredient.nutrients)
            stringBuilder.append(ingredientText).append(ingredientNutrientsText).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun buildCaloricBreakdownText(caloricBreakdown: CaloricBreakdown): String {
        val stringBuilder = StringBuilder("Caloric Breakdown:\n")
        stringBuilder.append("Protein: ${caloricBreakdown.percentProtein}%\n")
        stringBuilder.append("Fat: ${caloricBreakdown.percentFat}%\n")
        stringBuilder.append("Carbohydrates: ${caloricBreakdown.percentCarbs}%")
        return stringBuilder.toString()
    }

    private fun buildWeightPerServingText(weightPerServing: WeightPerServing): String {
        return "Weight per Serving: ${weightPerServing.amount} ${weightPerServing.unit}"
    }
}
