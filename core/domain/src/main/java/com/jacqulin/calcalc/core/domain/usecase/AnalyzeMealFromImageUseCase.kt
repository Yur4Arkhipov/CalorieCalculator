package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.ImageAnalysisResult
import com.jacqulin.calcalc.core.domain.model.Nutrition

interface AnalyzeMealFromImageUseCase {
    suspend operator fun invoke(imageBytes: ByteArray): ImageAnalysisResult
}