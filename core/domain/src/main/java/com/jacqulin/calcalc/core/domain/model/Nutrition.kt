package com.jacqulin.calcalc.core.domain.model

data class Nutrition(
    val name: String = "",
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbs: Double
)