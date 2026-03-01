package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

fun NutritionDto.toDomain() = Nutrition(
    calories,
    protein,
    fat,
    carbs
)