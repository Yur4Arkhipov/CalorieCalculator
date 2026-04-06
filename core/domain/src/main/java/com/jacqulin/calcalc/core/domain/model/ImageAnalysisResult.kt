package com.jacqulin.calcalc.core.domain.model

data class ImageAnalysisResult(
    val nutrition: Nutrition,
    val savedImagePath: String?
)