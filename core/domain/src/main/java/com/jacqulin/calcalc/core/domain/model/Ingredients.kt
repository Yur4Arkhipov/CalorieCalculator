package com.jacqulin.calcalc.core.domain.model

data class Ingredient(
    val name: String,
    val weight: Int,
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val carb: Int
)