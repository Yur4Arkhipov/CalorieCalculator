package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.model.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface MealRepository {
    fun observeDayData(date: Date): Flow<DayData>
    suspend fun addMeal(date: Date, meal: Meal)
}