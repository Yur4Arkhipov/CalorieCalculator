package com.jacqulin.calcalc.core.domain.model

data class MacroNutrients(
    val protein: Int = 0,
    val carb: Int = 0,
    val fat: Int = 0,
    val proteinsGoal: Float = 150f,
    val carbsGoal: Float = 250f,
    val fatsGoal: Float = 67f
)