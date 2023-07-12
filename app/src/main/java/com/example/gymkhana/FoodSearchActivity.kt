package com.example.gymkhana

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
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

    private lateinit var nutritionalSearchAutoComplete: AutoCompleteTextView
    private lateinit var nutritionalResultsTextView: TextView
    private lateinit var nutritionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)

        nutritionalSearchAutoComplete = findViewById(R.id.nutritionalSearchAutoComplete)
        nutritionalResultsTextView = findViewById(R.id.nutritionalResultsTextView)
        nutritionButton = findViewById(R.id.nutritionButton)

        val keywords = NutritionItem.nutritionList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, keywords)
        nutritionalSearchAutoComplete.setAdapter(adapter)

        nutritionButton.setOnClickListener {
            val selectedKeyword = nutritionalSearchAutoComplete.text.toString()
            val selectedItem = NutritionItem.nutritionList.find { it.name.equals(selectedKeyword, ignoreCase = true) }
            selectedItem?.let {
                val itemId = it.id
                launchCoroutine(itemId)
            }
        }
    }

    private fun launchCoroutine(itemId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val nutritionalData = fetchNutritionalInfo(itemId)

            withContext(Dispatchers.Main) {
                updateUI(nutritionalData)
            }
        }
    }

    private suspend fun fetchNutritionalInfo(itemId: Int): NutritionalData? {
        val response = apiService.getNutritionalInfo(itemId)

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
                    caloricBreakdown,
                    weightPerServing
                )
            }
        }

        return null
    }

    private fun updateUI(nutritionalData: NutritionalData?) {
        if (nutritionalData != null) {
            val nutrientsText = buildNutrientsText(nutritionalData.nutrients)
            val caloricBreakdownText = buildCaloricBreakdownText(nutritionalData.caloricBreakdown)
            val weightPerServingText = buildWeightPerServingText(nutritionalData.weightPerServing)

            val nutritionalInfoText = StringBuilder().apply {
                append(nutrientsText)
                append("\n\n")
                append(caloricBreakdownText)
                append("\n\n")
                append(weightPerServingText)
            }.toString()

            nutritionalResultsTextView.text = nutritionalInfoText
        } else {
            // Handle the case when the API request is not successful
            nutritionalResultsTextView.text = "Failed to fetch nutritional information"
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
