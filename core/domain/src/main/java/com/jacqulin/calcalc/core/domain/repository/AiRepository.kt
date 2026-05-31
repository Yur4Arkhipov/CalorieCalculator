package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

interface AiRepository {
    suspend fun analyzeMeal(description: String): Result <Nutrition, AppError>
    suspend fun analyzeMealFromImage(imageBase64: String): Result <Nutrition, AppError>
    suspend fun refineMeal(
        currentMeal: Nutrition,
        userPrompt: String
    ): Result <Nutrition, AppError>
}