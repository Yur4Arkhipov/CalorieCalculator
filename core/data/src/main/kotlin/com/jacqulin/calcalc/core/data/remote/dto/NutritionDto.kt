package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Nutrition
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

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
    weight = weight.roundToInt(),
    calories = calories.roundToInt(),
    protein = protein.roundToInt(),
    fat = fat.roundToInt(),
    carb = carb.roundToInt(),
    ingredient = ingredient.map { it.toDomain() }
)