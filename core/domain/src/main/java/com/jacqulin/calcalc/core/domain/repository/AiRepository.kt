package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.Nutrition

interface AiRepository {
    suspend fun analyzeMeal(description: String): Nutrition
}