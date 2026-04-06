package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.UpdateMealUseCase

class UpdateMealUseCaseImpl(
    private val mealRepository: MealRepository
) : UpdateMealUseCase {
    override suspend fun invoke(meal: Meal) {
        mealRepository.updateMeal(meal)
    }
}