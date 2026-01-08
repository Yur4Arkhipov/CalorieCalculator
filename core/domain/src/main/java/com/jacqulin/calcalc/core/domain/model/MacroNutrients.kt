package com.jacqulin.calcalc.core.domain.model

data class MacroNutrients(
    val proteins: Float = 0f,
    val carbs: Float = 0f,
    val fats: Float = 0f,
    val proteinsGoal: Float = 150f,
    val carbsGoal: Float = 250f,
    val fatsGoal: Float = 67f
)