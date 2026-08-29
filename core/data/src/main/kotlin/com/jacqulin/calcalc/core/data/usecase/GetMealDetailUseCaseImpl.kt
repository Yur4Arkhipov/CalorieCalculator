package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GetMealDetailUseCase
import javax.inject.Inject

class GetMealDetailUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GetMealDetailUseCase {
    override suspend fun invoke(mealId: Int): Meal {
        return mealRepository.getMealDetail(mealId)
    }
}