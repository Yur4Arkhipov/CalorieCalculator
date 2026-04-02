package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.data.local.dao.MealDao
import com.jacqulin.calcalc.core.data.local.entities.MealComponentEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import com.jacqulin.calcalc.core.data.local.entities.toDomain
import com.jacqulin.calcalc.core.domain.model.DayData
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : MealRepository {

    private fun getDateKey(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
    }

    override fun observeDayData(date: Date): Flow<DayData> {
        val dateKey = getDateKey(date)

        return combine(
            mealDao.observeMealsWithComponentsForDate(dateKey),
            userPreferencesRepository.observeUserProfile()
        ) { mealsWithComponents, profile ->

            val meals = mealsWithComponents.map { it.toDomain() }

            val macros = MacroNutrients(
                calories = meals.sumOf { it.calories },
                protein = meals.sumOf { it.proteins },
                carb = meals.sumOf { it.carbs },
                fat = meals.sumOf { it.fats },
                caloriesGoal = profile.caloriesGoal,
                proteinsGoal = profile.proteinGoal,
                carbsGoal = profile.carbsGoal,
                fatsGoal = profile.fatGoal
            )

            DayData(
                meals = meals,
                macros = macros
            )
        }
    }

    override fun observeFavoriteMeals(): Flow<List<Meal>> {
        return mealDao.observeFavoriteMealsWithComponents()
            .map { mealsWithComponents -> mealsWithComponents.map { it.toDomain() } }
    }

    override suspend fun addMeal(date: Date, meal: Meal) {
        val dateKey = getDateKey(date)
        val mealId = mealDao.insertMeal(
            MealEntity(
                name = meal.name,
                calories = 0, // will be calculated from components
                protein = 0,
                fat = 0,
                carbs = 0,
                time = meal.time,
                type = meal.type,
                date = dateKey,
                imageUri = meal.imageUri
            )
        ).toInt()

        // Insert components
        meal.components.forEach { component ->
            mealDao.insertComponent(
                MealComponentEntity(
                    mealId = mealId,
                    name = component.name,
                    calories = component.calories,
                    protein = component.protein,
                    fat = component.fat,
                    carbs = component.carbs
                )
            )
        }
    }

    override suspend fun updateMeal(meal: Meal) {
        val existing = mealDao.getMealById(meal.id) ?: return
        mealDao.updateMeal(
            existing.copy(
                name = meal.name,
                calories = 0, // will be calculated from components
                protein = 0,
                fat = 0,
                carbs = 0,
                imageUri = meal.imageUri,
                isFavorite = meal.isFavorite
            )
        )

        // Update components
        mealDao.deleteComponentsByMealId(meal.id)
        meal.components.forEach { component ->
            mealDao.insertComponent(
                MealComponentEntity(
                    id = component.id.takeIf { it != 0 } ?: 0,
                    mealId = meal.id,
                    name = component.name,
                    calories = component.calories,
                    protein = component.protein,
                    fat = component.fat,
                    carbs = component.carbs
                )
            )
        }
    }

    override suspend fun deleteMeal(meal: Meal) {
        val existing = mealDao.getMealById(meal.id) ?: return
        mealDao.deleteMeal(existing)
        // Components will be deleted via CASCADE in foreign key
    }
}