package com.jacqulin.calcalc.core.domain.model

data class Ingredient(
    val name: String,
    val weight: Double,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carb: Double
)