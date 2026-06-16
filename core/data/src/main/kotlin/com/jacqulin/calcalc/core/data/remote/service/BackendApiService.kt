package com.jacqulin.calcalc.core.data.remote.service

import com.jacqulin.calcalc.core.data.remote.dto.NutritionDto
import com.jacqulin.calcalc.core.data.remote.dto.backend.AnalyzeImageRequest
import com.jacqulin.calcalc.core.data.remote.dto.backend.AnalyzeTextRequest
import com.jacqulin.calcalc.core.data.remote.dto.backend.RefineMealRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendApiService {

    @POST("analyze-text")
    suspend fun analyzeText(
        @Body request: AnalyzeTextRequest
    ): NutritionDto

    @POST("analyze-image")
    suspend fun analyzeImage(
        @Body request: AnalyzeImageRequest
    ): NutritionDto

    @POST("refine")
    suspend fun refineMeal(
        @Body request: RefineMealRequest
    ): NutritionDto
}