package com.jacqulin.calcalc.core.data.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import java.util.Date
import javax.inject.Inject

class SaveManualAddMealDBUseCaseImpl @Inject constructor(
    private val mealRepository: MealRepository
) : SaveManualAddMealDBUseCase {

    override suspend fun invoke(date: Date, meal: Meal) {
        mealRepository.addMeal(date, meal)
    }
}