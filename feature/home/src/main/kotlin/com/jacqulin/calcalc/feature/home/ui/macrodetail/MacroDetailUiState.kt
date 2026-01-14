package com.jacqulin.calcalc.feature.home.ui.macrodetail

import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal

data class MacroDetailUiState(
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 2000,
    val dailyCaloriesGoal: Int = 2000,
    val mealsToday: List<Meal> = emptyList(),
    val todayMacros: MacroNutrients = MacroNutrients(0, 0, 0, 150f, 240f, 67f),
    val isLoading: Boolean = true,
    val editingMeal: Meal? = null,
    val isEditingSheetOpen: Boolean = false
)