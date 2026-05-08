package com.jacqulin.calcalc.feature.home.ui.review

data class MealReviewUiState(
    val isLoading: Boolean = false,
    val isError: String? = null,
    val name: String = "",
    val calories: String = "",
    val proteins: String = "",
    val fats: String = "",
    val carbs: String = "",
    val weight: String = ""
)