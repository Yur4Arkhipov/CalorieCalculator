package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.model.Meal
import java.util.Date

interface MealRepository {
    suspend fun getDayData(date: Date): DayData
    suspend fun addMeal(date: Date, meal: Meal)
}