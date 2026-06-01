package com.jacqulin.calcalc.feature.home.ui.mealdetail

import com.jacqulin.calcalc.core.domain.model.Meal

data class MealDetailUiState(
    val isLoading: Boolean = false,
    val meal: Meal? = null
)