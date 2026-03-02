package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Meal

interface UpdateMealUseCase {
    suspend operator fun invoke(meal: Meal)
}