package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.RefineMealUseCase
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

class RefineMealUseCaseImpl(
    private val aiRepository: AiRepository
) : RefineMealUseCase {
    override suspend fun invoke(
        currentMeal: Nutrition,
        userPrompt: String
    ): Result<Nutrition, AppError> {
        return aiRepository.refineMeal(
            currentMeal = currentMeal,
            userPrompt = userPrompt
        )
    }
}