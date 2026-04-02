package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.model.NutritionNew

interface AiRepository {
    suspend fun analyzeMeal(description: String): Nutrition
    suspend fun analyzeMealFromImage(imageBase64: String): NutritionNew
}