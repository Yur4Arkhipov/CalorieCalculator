package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

interface RefineMealUseCase {
    suspend operator fun invoke(currentMeal: Nutrition, userPrompt: String): Result<Nutrition, AppError>
}