package com.jacqulin.calcalc.feature.home.ui.mealdetail

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.feature.home.ui.review.IngredientUi

data class MealDetailUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSaveIconEnable: Boolean = false,
    val meal: Meal? = null,
    val name: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbs: String = "",
    val weight: String = "",
    val ingredients: List<IngredientUi> = emptyList(),
    val description: String = "",
    val selectedMealType: MealType = MealType.BREAKFAST,
    val isFavoriteMeal: Boolean = false
)