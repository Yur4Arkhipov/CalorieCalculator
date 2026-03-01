package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase

class AnalyzeMealUseCaseImpl(
    private val aiRepository: AiRepository
) : AnalyzeMealUseCase {
    override suspend fun invoke(description: String): Nutrition {
        return aiRepository.analyzeMeal(description)
    }
}