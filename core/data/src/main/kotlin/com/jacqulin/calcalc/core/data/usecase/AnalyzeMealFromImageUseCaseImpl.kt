package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase

class AnalyzeMealFromImageUseCaseImpl(
    private val aiRepository: AiRepository
) : AnalyzeMealFromImageUseCase {
    override suspend fun invoke(imageBase64: String): Nutrition {
        return aiRepository.analyzeMealFromImage(imageBase64)
    }
}