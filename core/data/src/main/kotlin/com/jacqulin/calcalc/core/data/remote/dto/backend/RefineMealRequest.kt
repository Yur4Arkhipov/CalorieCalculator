package com.jacqulin.calcalc.core.data.remote.dto.backend

import com.jacqulin.calcalc.core.data.remote.dto.NutritionDto
import kotlinx.serialization.Serializable

@Serializable
data class RefineMealRequest(
    val currentMeal: NutritionDto,
    val userPrompt: String

)
