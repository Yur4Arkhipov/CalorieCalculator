package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.DeleteMealUseCase

class DeleteMealUseCaseImpl(
    private val mealRepository: MealRepository
) : DeleteMealUseCase {
    override suspend fun invoke(meal: Meal) {
        mealRepository.deleteMeal(meal)
    }
}