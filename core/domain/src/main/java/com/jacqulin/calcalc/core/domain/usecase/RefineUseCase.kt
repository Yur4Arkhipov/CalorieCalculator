package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition

interface RefineMealUseCase {
    suspend operator fun invoke(
        currentMeal: Nutrition,
        userPrompt: String
    ): Nutrition
}