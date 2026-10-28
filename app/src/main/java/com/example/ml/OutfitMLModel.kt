package com.example.ml

import android.content.Context
import android.util.Log

class OutfitMLModel(private val context: Context) {

    private val prefs = context.getSharedPreferences("outfit_ml_model_prefs", Context.MODE_PRIVATE)

    // ML Weights
    var colorWeight: Double = prefs.getFloat("colorWeight", 0.4f).toDouble()
    var styleWeight: Double = prefs.getFloat("styleWeight", 0.3f).toDouble()
    var occasionWeight: Double = prefs.getFloat("occasionWeight", 0.2f).toDouble()
    var userPrefWeight: Double = prefs.getFloat("userPrefWeight", 0.1f).toDouble()

    // Learning rate
    private val alpha = 0.05

    fun predict(
        colorComp: Double,
        styleComp: Double,
        occasionMatch: Double,
        userPref: Double
    ): Double {
        val score = (colorWeight * colorComp) +
                    (styleWeight * styleComp) +
                    (occasionWeight * occasionMatch) +
                    (userPrefWeight * userPref)
        return score.coerceIn(10.0, 100.0)
    }

    fun learn(
        colorComp: Double,
        styleComp: Double,
        occasionMatch: Double,
        userPref: Double,
        liked: Boolean
    ) {
        val target = if (liked) 100.0 else 10.0
        val currentScore = predict(colorComp, styleComp, occasionMatch, userPref)
        val error = target - currentScore

        // Adjust weights based on error and input values (Stochastic Gradient Descent style)
        // Normalize inputs for weight adjustment
        colorWeight += alpha * error * (colorComp / 100.0)
        styleWeight += alpha * error * (styleComp / 100.0)
        occasionWeight += alpha * error * (occasionMatch / 100.0)
        userPrefWeight += alpha * error * (userPref / 100.0)

        // Normalize weights so they sum to ~1.0
        val totalWeight = colorWeight + styleWeight + occasionWeight + userPrefWeight
        if (totalWeight > 0) {
            colorWeight /= totalWeight
            styleWeight /= totalWeight
            occasionWeight /= totalWeight
            userPrefWeight /= totalWeight
        }

        saveWeights()
    }

    private fun saveWeights() {
        prefs.edit().apply {
            putFloat("colorWeight", colorWeight.toFloat())
            putFloat("styleWeight", styleWeight.toFloat())
            putFloat("occasionWeight", occasionWeight.toFloat())
            putFloat("userPrefWeight", userPrefWeight.toFloat())
            apply()
        }
        Log.d("OutfitMLModel", "Weights updated: C=${colorWeight.toFloat()}, S=${styleWeight.toFloat()}, O=${occasionWeight.toFloat()}, U=${userPrefWeight.toFloat()}")
    }
}
