package com.jacqulin.calcalc.core.domain.model

data class Meal(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val proteins: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val time: String,
    val type: MealType,
    val imageUri: String? = null
)

enum class MealType(val displayName: String) {
    BREAKFAST("Завтрак"),
    LUNCH("Обед"),
    DINNER("Ужин"),
    SNACK("Перекус")
}