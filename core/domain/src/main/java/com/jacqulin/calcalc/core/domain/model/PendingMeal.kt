package com.jacqulin.calcalc.core.domain.model

import java.util.UUID

data class PendingMeal(
    val type: MealType,
    val id: String = UUID.randomUUID().toString(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val imageUri: String? = null
)