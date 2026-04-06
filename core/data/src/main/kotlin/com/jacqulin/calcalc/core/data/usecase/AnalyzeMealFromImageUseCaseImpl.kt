package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.ImageAnalysisResult
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase

class AnalyzeMealFromImageUseCaseImpl(
    private val aiRepository: AiRepository,
    private val imageRepository: ImageRepository
) : AnalyzeMealFromImageUseCase {
    override suspend fun invoke(imageBytes: ByteArray): ImageAnalysisResult {
        val base64 = imageRepository.encodeForAi(imageBytes)
        val nutrition = aiRepository.analyzeMealFromImage(base64)
        val savedPath = imageRepository.saveImage(imageBytes)
        return ImageAnalysisResult(nutrition = nutrition, savedImagePath = savedPath)
    }
}