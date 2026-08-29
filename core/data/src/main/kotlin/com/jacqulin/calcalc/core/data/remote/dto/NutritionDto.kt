package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val name: String? = null,
    val weight: Int,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int,
    val ingredients: List<IngredientDto> = emptyList()
)

fun NutritionDto.toDomain() = Nutrition(
    name = name ?: "",
    weight = weight,
    calories = calories,
    protein = protein,
    fat = fat,
    carb = carb,
    ingredient = ingredients.map { it.toDomain() }
)

fun Nutrition.toDto(): NutritionDto {
    return NutritionDto(
        name = name,
        weight = weight,
        calories = calories,
        protein = protein,
        fat = fat,
        carb = carb,
        ingredients = ingredient.map { it.toDto() }
    )
}