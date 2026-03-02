package com.jacqulin.calcalc.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jacqulin.calcalc.core.data.local.entities.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM meal WHERE date = :date ORDER BY time ASC")
    fun observeMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal WHERE id = :id LIMIT 1")
    suspend fun getMealById(id: Int): MealEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}