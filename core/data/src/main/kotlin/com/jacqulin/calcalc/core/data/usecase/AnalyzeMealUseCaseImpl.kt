package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiAccessRepository
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealUseCase
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

class AnalyzeMealUseCaseImpl(
    private val aiRepository: AiRepository,
    private val aiAccessRepository: AiAccessRepository
) : AnalyzeMealUseCase {
    override suspend fun invoke(description: String): Result<Nutrition, AppError> {
        if (!aiAccessRepository.isAccessAllowed()) {
            return Result.Error(AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS))
        }

        val result = aiRepository.analyzeMeal(description)

        if (result is Result.Error &&
            result.error == AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS)
        ) {
            aiAccessRepository.markLimitReached()
        }

        return result
    }
}