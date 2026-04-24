package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.GetFavoriteMealsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteMealsUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : GetFavoriteMealsUseCase {

    override fun invoke(): Flow<List<Meal>> {
        return mealRepository.observeFavoriteMeals()
    }
}