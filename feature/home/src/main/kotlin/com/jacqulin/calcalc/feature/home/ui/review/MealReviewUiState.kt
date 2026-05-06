package com.jacqulin.calcalc.feature.home.ui.review

import com.jacqulin.calcalc.core.domain.model.Meal

data class MealReviewUiState(
    val meal: Meal? = null,
    val isLoading: Boolean = false,
    val isError: String? = null,
    val name: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbs: String = ""
)