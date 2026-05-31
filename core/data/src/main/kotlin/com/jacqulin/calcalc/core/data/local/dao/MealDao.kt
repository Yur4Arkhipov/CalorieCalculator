package com.jacqulin.calcalc.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jacqulin.calcalc.core.data.local.entities.IngredientEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time ASC")
    fun observeMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavoriteMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Int): MealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Query("UPDATE meal SET isFavorite = 0 WHERE id IN (:ids)")
    suspend fun removeFromFavorites(ids: List<Int>)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Transaction
    suspend fun insertMealWithIngredients(meal: MealEntity, ingredients: List<IngredientEntity>) {
        val generatedMealId = insertMeal(meal).toInt()
        val ingredientsWithMealId = ingredients.map { it.copy(mealId = generatedMealId) }
        insertIngredients(ingredientsWithMealId)
    }
}