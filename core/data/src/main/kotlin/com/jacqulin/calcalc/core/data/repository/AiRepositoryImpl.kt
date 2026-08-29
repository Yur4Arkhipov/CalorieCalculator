package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.data.remote.dto.backend.AnalyzeImageRequest
import com.jacqulin.calcalc.core.data.remote.dto.backend.AnalyzeTextRequest
import com.jacqulin.calcalc.core.data.remote.dto.backend.RefineMealRequest
import com.jacqulin.calcalc.core.data.remote.dto.toDomain
import com.jacqulin.calcalc.core.data.remote.dto.toDto
import com.jacqulin.calcalc.core.data.remote.service.BackendApiService
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError
import com.jacqulin.calcalc.core.util.errors.ErrorHandler
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiApi: BackendApiService
) : AiRepository {

    override suspend fun analyzeMeal(description: String): Result<Nutrition, AppError> {
        return try {
            val request = AnalyzeTextRequest(description)
            val response = aiApi.analyzeText(request)

            if (response.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success(response.toDomain())
        } catch (e: Throwable) {
            Result.Error(ErrorHandler.mapError(e))
        }
    }

    override suspend fun analyzeMealFromImage(imageBase64: String): Result <Nutrition, AppError> {
        return try {
            val request = AnalyzeImageRequest(imageBase64)
            val response = aiApi.analyzeImage(request)

            if (response.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success(response.toDomain())
        } catch (e: Throwable) {
            Result.Error(ErrorHandler.mapError(e))
        }
    }

    override suspend fun refineMeal(
        currentMeal: Nutrition,
        userPrompt: String
    ): Result<Nutrition, AppError> {
        return try {
            val request = RefineMealRequest(
                currentMeal = currentMeal.toDto(),
                userPrompt = userPrompt
            )
            val response = aiApi.refineMeal(request)

            if (response.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success(response.toDomain())
        } catch (e: Throwable) {
            Result.Error(ErrorHandler.mapError(e))
        }
    }
}