package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

class AnalyzeMealUseCaseImpl(
    private val aiRepository: AiRepository
) : AnalyzeMealUseCase {
    override suspend fun invoke(description: String): Result<Nutrition, AppError> {
        return aiRepository.analyzeMeal(description)
    }
}