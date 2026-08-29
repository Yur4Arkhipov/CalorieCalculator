package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError

interface AnalyzeMealFromImageUseCase {
    suspend operator fun invoke(imageBytes: ByteArray): Result<Nutrition, AppError>
}