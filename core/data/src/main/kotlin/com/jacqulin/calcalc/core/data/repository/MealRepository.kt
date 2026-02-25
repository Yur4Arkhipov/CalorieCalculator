package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import com.jacqulin.calcalc.core.data.local.entities.toDomain
import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {

    private fun getDateKey(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    override fun observeDayData(date: Date): Flow<DayData> {
        val dateKey = getDateKey(date)

        return mealDao.observeMealsForDate(dateKey)
            .map { entities ->

                val meals = entities.map { it.toDomain() }

                val macros = MacroNutrients(
                    protein = meals.sumOf { it.proteins },
                    carb = meals.sumOf { it.carbs },
                    fat = meals.sumOf { it.fats },
                    proteinsGoal = 150f,
                    carbsGoal = 250f,
                    fatsGoal = 67f
                )

                DayData(
                    meals = meals,
                    macros = macros
                )
            }
    }

    override suspend fun addMeal(date: Date, meal: Meal) {
        val dateKey = getDateKey(date)
        mealDao.insertMeal(
            MealEntity(
                name = meal.name,
                calories = meal.calories,
                protein = meal.proteins,
                fat = meal.fats,
                carbs = meal.carbs,
                time = meal.time,
                type = meal.type,
                date = dateKey
            )
        )
    }
}