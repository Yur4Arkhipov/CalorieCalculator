package com.jacqulin.calcalc.core.domain.model

data class DayData(
    val meals: List<Meal> = emptyList(),
    val macros: MacroNutrients = MacroNutrients()
)