package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Ingredient
import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val name: String,
    val weight: Int,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int
)

fun IngredientDto.toDomain() = Ingredient(
    name = name,
    weight = weight,
    calories = calories,
    protein = protein,
    fat = fat,
    carb = carb
)