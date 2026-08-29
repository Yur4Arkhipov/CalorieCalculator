package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiAccessRepository
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

class AnalyzeMealFromImageUseCaseImpl(
    private val aiRepository: AiRepository,
    private val imageRepository: ImageRepository,
    private val aiAccessRepository: AiAccessRepository
) : AnalyzeMealFromImageUseCase {
    override suspend fun invoke(imageBytes: ByteArray): Result<Nutrition, AppError> {
        if (!aiAccessRepository.isAccessAllowed()) {
            return Result.Error(AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS))
        }

        val base64 = imageRepository.encodeForAi(imageBytes)
        val nutrition = aiRepository.analyzeMealFromImage(base64)

        if (nutrition is Result.Error &&
            nutrition.error == AppError.HttpError(AppError.Http.TOO_MANY_REQUESTS)
        ) {
            aiAccessRepository.markLimitReached()
        }

        return nutrition
    }
}