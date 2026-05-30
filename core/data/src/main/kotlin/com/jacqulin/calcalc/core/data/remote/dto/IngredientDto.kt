package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Ingredient
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class IngredientDto(
    val name: String,
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double
)

fun IngredientDto.toDomain() = Ingredient(
    name = name,
    weight = weight.roundToInt(),
    calories = calories.roundToInt(),
    protein = protein.roundToInt(),
    fat = fat.roundToInt(),
    carb = carb.roundToInt()
)