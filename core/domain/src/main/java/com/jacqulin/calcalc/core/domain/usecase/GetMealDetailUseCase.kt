package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Meal

interface GetMealDetailUseCase {
    suspend operator fun invoke(mealId: Int): Meal
}