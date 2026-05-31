package com.jacqulin.calcalc.feature.home.ui.review

import com.jacqulin.calcalc.core.domain.model.MealType

data class MealReviewUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isErrorRefine: Boolean = false,
    val errorText: String? = null,
    val isSaved: Boolean = false,
    val name: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbs: String = "",
    val weight: String = "",
    val ingredients: List<IngredientUi> = emptyList(),
    val description: String = "",
    val selectedMealType: MealType = MealType.BREAKFAST,
    val isProcessingDescription: Boolean = false

)

data class IngredientUi(
    val name: String,
    val weight: String,
    val calories: String,
    val protein: String,
    val fat: String,
    val carb: String
)