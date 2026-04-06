package com.jacqulin.calcalc.core.domain.model

data class MacroNutrients(
    val calories: Int,
    val protein: Int,
    val carb: Int,
    val fat: Int,
    val caloriesGoal: Int,
    val proteinsGoal: Int,
    val carbsGoal: Int,
    val fatsGoal: Int
)