package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiAccessRepository
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.usecase.RefineMealUseCase
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

class RefineMealUseCaseImpl(
    private val aiRepository: AiRepository,
    private val aiAccessRepository: AiAccessRepository
) : RefineMealUseCase {
    override suspend fun invoke(
        currentMeal: Nutrition,
        userPrompt: String
    ): Result<Nutrition, AppError> {
        if (!aiAccessRepository.isAccessAllowed()) {
            return Result.Error(AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS))
        }

        val result = aiRepository.refineMeal(
            currentMeal = currentMeal,
            userPrompt = userPrompt
        )

        if (result is Result.Error &&
            result.error == AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS)
        ) {
            aiAccessRepository.markLimitReached()
        }

        return result
    }
}