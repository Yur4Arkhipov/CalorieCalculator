package com.jacqulin.calcalc.core.data.remote.dto

import com.jacqulin.calcalc.core.domain.model.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val name: String? = null,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)

fun NutritionDto.toDomain() = Nutrition(
    name = name ?: "",
    calories,
    protein,
    fat,
    carbs
)