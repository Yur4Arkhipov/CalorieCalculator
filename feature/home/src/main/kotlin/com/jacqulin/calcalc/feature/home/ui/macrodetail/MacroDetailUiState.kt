package com.jacqulin.calcalc.feature.home.ui.macrodetail

import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal

data class MacroDetailUiState(
    val consumedCalories: Int = 0,
    val remainingCalories: Int = 0,
    val dailyCaloriesGoal: Int = 0,
    val mealsToday: List<Meal> = emptyList(),
    val todayMacros: MacroNutrients = MacroNutrients(
        calories = 0,
        protein = 0,
        carb = 0,
        fat = 0,
        caloriesGoal = 0,
        proteinsGoal = 0,
        carbsGoal = 0,
        fatsGoal = 0
    ),
    val isLoading: Boolean = true,
    val editingMeal: Meal? = null,
    val isEditingSheetOpen: Boolean = false
)