package com.jacqulin.calcalc.core.domain.model

data class Nutrition(
    val name: String = "",
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

data class NutritionNew(
    val name: String,
    val total: Macros,
    val items: List<FoodItem>
)

data class FoodItem(
    val name: String,
    val weight: Double,
    val macros: Macros
)

data class Macros(
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)