package com.jacqulin.calcalc.core.domain.usecase

import com.jacqulin.calcalc.core.domain.model.Meal
import kotlinx.coroutines.flow.Flow

interface GetFavoriteMealsUseCase {
    operator fun invoke(): Flow<List<Meal>>
}