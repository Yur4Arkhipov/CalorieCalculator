package com.jacqulin.calcalc.core.domain.model

data class ImageAnalysisResult(
    val nutrition: NutritionNew,
    val savedImagePath: String?
)