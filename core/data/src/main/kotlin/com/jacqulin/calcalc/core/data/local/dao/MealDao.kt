package com.jacqulin.calcalc.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jacqulin.calcalc.core.data.local.entities.MealComponentEntity
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import com.jacqulin.calcalc.core.data.local.entities.MealWithComponents
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Transaction
    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time ASC")
    fun observeMealsWithComponentsForDate(date: String): Flow<List<MealWithComponents>>

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time ASC")
    fun observeMealsForDate(date: String): Flow<List<MealEntity>>

    @Transaction
    @Query("SELECT * FROM meal WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavoriteMealsWithComponents(): Flow<List<MealWithComponents>>

    @Query("SELECT * FROM meal WHERE isFavorite = 1 ORDER BY name ASC")
    fun observeFavoriteMeals(): Flow<List<MealEntity>>

    @Transaction
    @Query("SELECT * FROM meal WHERE id = :id LIMIT 1")
    suspend fun getMealWithComponentsById(id: Int): MealWithComponents?

    @Query("SELECT * FROM meal WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Int): MealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: MealComponentEntity)

    @Update
    suspend fun updateComponent(component: MealComponentEntity)

    @Delete
    suspend fun deleteComponent(component: MealComponentEntity)

    @Query("SELECT * FROM meal_component WHERE mealId = :mealId")
    suspend fun getComponentsByMealId(mealId: Int): List<MealComponentEntity>

    @Query("DELETE FROM meal_component WHERE mealId = :mealId")
    suspend fun deleteComponentsByMealId(mealId: Int)
}