package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.RefineMealUseCase

class RefineMealUseCaseImpl(
    private val aiRepository: AiRepository
) : RefineMealUseCase {
    override suspend fun invoke(
        currentMeal: Nutrition,
        userPrompt: String
    ): Nutrition {
        return aiRepository.refineMeal(
            currentMeal = currentMeal,
            userPrompt = userPrompt
        )
    }
}