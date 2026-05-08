package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val name: String? = null,
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double,
    val ingredient: List<IngredientDto> = emptyList()
)

fun NutritionDto.toDomain() = Nutrition(
    name = name ?: "",
    weight = weight,
    calories = calories,
    protein = protein,
    fat = fat,
    carb = carb,
    ingredient = ingredient.map { it.toDomain() }
)